package com.gatisnau.gati.presenter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.gatisnau.gati.BuildConfig;
import com.gatisnau.gati.R;
import com.gatisnau.gati.listeners.OnImageClickListener;
import com.gatisnau.gati.listeners.OnImageDownloaded;
import com.gatisnau.gati.model.AppModel;
import com.gatisnau.gati.model.DateManager;
import com.gatisnau.gati.model.Model;
import com.gatisnau.gati.model.ScheduleObject;
import com.gatisnau.gati.model.network.NetworkManager;
import com.gatisnau.gati.model.network.UpdateApp;
import com.gatisnau.gati.utils.GatiPermissions;
import com.gatisnau.gati.utils.GatiPreferences;
import com.gatisnau.gati.utils.StackFragment;
import com.gatisnau.gati.view.FragmentImagePreview;
import com.gatisnau.gati.view.cardview.CardFragment;
import com.gatisnau.gati.view.cardview.RecyclerCardAdapter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {

    public static final int FULL_SCHEDULE = 1;
    public static final int CORRESPONDENCE_SCHEDULE = 2;
    public static final int REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE = 159;

    @Nullable private Activity activity;
    @Nullable private Context context;
    private Handler handlerUI;
    private Thread backgroundThread;
    private Model model;
    private StackFragment stackFragment;
    private RecyclerCardAdapter recyclerAdapter;
    private DateManager date;
    private NetworkManager network;

    private int currentDay = 0;
    private int type;

    private ArrayList<Bitmap> fullTimeSchedule;
    private ArrayList<Bitmap> correspondenceSchedule;

    public Presenter(@Nullable Context context) {
        this.context = context;
        this.model = new AppModel();
        this.date = new DateManager();
        this.network = new NetworkManager();
        this.handlerUI = new Handler();
        this.fullTimeSchedule = createBitmapList(5);
        this.correspondenceSchedule = createBitmapList(5);
        this.type = GatiPreferences.getTypeSchedule(context);
    }


    /* ----------interface---------- */

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.stackFragment = new StackFragment(fragmentManager, R.id.fragment_container);
    }

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        this.recyclerAdapter = adapter;
        adapter.setSchedulers(getScheduleList(type));
        adapter.setImageClickListener(imageClickListener);
        adapter.toPosition(currentDay);
        downloadImage(type);
    }

    public void changeSchedule(int type) {
        this.type = type;
        GatiPreferences.setTypeSchedule(context, type);
        if (type == FULL_SCHEDULE) {
            stackFragment.setAnimation(R.animator.slide_in_right_start, R.animator.slide_in_right_end);
        } else {
            stackFragment.setAnimation(R.animator.slide_in_left_start, R.animator.slide_in_left_end);
        }
        stackFragment.replaceFragment(CardFragment.newInstance(), true);
    }

    public void startActivity(String packageActivity, String uri) {
        if (context == null) return;
        Uri link = Uri.parse(uri);
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, link);
        activityIntent.setPackage(packageActivity);
        try {
            context.startActivity(activityIntent);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, link));
        }
    }

    public void attachActivity(Activity activity) {
        this.activity = activity;
        this.context = activity;
    }

    /**
     * call this method when view is destroy
     */
    public void destroy() {
        context = null;
        activity = null;
    }

    public void setItem(int index) {
        recyclerAdapter.toPosition(index);
    }

    public void shareImage(int index) {
        if (getScheduleList(type) != null) {
            shareImage(getScheduleList(type).get(index));
        }
    }

    public void shareFailure() {
        Toast.makeText(context, R.string.share_is_failure, Toast.LENGTH_LONG).show();
    }

    public void sendEmail(String email) {
        if (context == null) return;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.send_email)));
    }

    public void updateApp() {
        new UpdateApp(context, handlerUI).startUpdate(BuildConfig.URL_VERSION_CONTROLL);
    }

    /* ----------internal logic---------- */

    public class ActivityLifecycleListener implements LifecycleObserver {
        private UpdateApp update;
        private boolean isFirstCreate = true;
        private int afterType = 0;

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            if (isFirstCreate) {
                isFirstCreate = false;
                update = new UpdateApp(context, handlerUI);
                update.checkVersion(BuildConfig.URL_VERSION_CONTROLL);
            }
            stackFragment.replaceFragment(CardFragment.newInstance(), false);
            CalculateCurrentPosition();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            if (update != null)
                update.stop();
            update = null;
        }
    }

    private void CalculateCurrentPosition() {
        int currentDay = date.getDayOfWeek();
        if (currentDay > 0 && currentDay < 6) {
            this.currentDay = currentDay;
        }
    }

    private void downloadImage(final int type) {
        backgroundThread = new Thread(() -> {
            try {
                if (checkInternetConnection() == false) return;

                List<ScheduleObject.Schedule> schedulers = model.getExistingSchedule();
                schedulers = getNeededSchedule(schedulers, type);
                for (ScheduleObject.Schedule schedule : schedulers) {
                    int index = date.getDayOfWeek(schedule);
                    if (isImageExist(type, index)) continue;
                    model.downloadImage(schedule, downloadListener);
                    System.out.println("start download " + schedule);
                }
            } catch (IOException | ParseException e) {
                Log.e(this.getClass().getName(), "downloadImage: ", e);
            }
        });
        backgroundThread.start();
    }

    private void shareImage(Bitmap bitmap) {
        if (context == null) return;

        if (GatiPermissions.checkWritePermissions(context)) {
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Розклад", "розклад за якийсь день");
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            context.startActivity(intent);
        } else {
            if (activity != null) {
                GatiPermissions.requestReadWrite(activity, REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE);
            } else {
                shareFailure();
            }
        }
    }

    private boolean isImageExist(int type, int index) {
        return index < 0 || index >= 5 || getScheduleList(type) == null || getScheduleList(type).get(index) != null;
    }

    // TODO: 4/21/19 alerts
    private boolean checkInternetConnection() {
        if (!network.isNetworkAvailable(context)) {
            return false;
        }
//        if (model.isInternetAvailable() == false){
//            return false;
//        }
        return true;
    }

    private ArrayList<Bitmap> getScheduleList(int type) {
        if (type == FULL_SCHEDULE) {
            return fullTimeSchedule;
        } else if (type == CORRESPONDENCE_SCHEDULE) {
            return correspondenceSchedule;
        } else {
            return null;
        }
    }

    private List<ScheduleObject.Schedule> getNeededSchedule(final List<ScheduleObject.Schedule> schedulers, int type) {
        List<ScheduleObject.Schedule> list = getTheRight(schedulers, type, true);
        if (!list.isEmpty()) {
            return list;
        } else {
            return getTheRight(schedulers, type, false);
        }
    }

    private List<ScheduleObject.Schedule> getTheRight(final List<ScheduleObject.Schedule> schedulers, int type, boolean isActual) {
        List<ScheduleObject.Schedule> list = new ArrayList<>();
        if (isActual) {
            for (ScheduleObject.Schedule schedule : schedulers) {
                if (!date.isScheduleAtThisWeek(schedule) || schedule.getType() != type) continue;
                list.add(schedule);
            }
        } else {
            this.currentDay = 0;
            int size = schedulers.size() <= 5 ? schedulers.size() : 5;
            for (int i = 0; i < size; i++) {
                if (schedulers.get(i).getType() != type) continue;
                list.add(schedulers.get(i));
            }
        }
        return list;
    }

    private ArrayList<Bitmap> createBitmapList(int size) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            bitmaps.add(null);
        }
        return bitmaps;
    }


    /* ----------listeners---------- */

    private OnImageDownloaded downloadListener = (image, schedule) -> {
        int index = date.getDayOfWeek(schedule);
        if (schedule.getType() == Presenter.FULL_SCHEDULE) {
            fullTimeSchedule.set(index, image);
        } else if (schedule.getType() == Presenter.CORRESPONDENCE_SCHEDULE) {
            correspondenceSchedule.set(index, image);
        }
        handlerUI.post(() -> {
            if (recyclerAdapter == null) return;
            recyclerAdapter.notifyItemChanged(index);
            if (index == currentDay) {
                recyclerAdapter.toPosition(currentDay);
            }
        });
    };

    private OnImageClickListener imageClickListener = (bitmap) -> {
        FragmentImagePreview fragmentImage = FragmentImagePreview.newInstance();
        fragmentImage.setBitmap(bitmap);
        stackFragment.addToStackFragment(fragmentImage);
        System.gc();
    };
}