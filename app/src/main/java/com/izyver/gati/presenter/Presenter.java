package com.izyver.gati.presenter;

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
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.izyver.gati.BuildConfig;
import com.izyver.gati.R;
import com.izyver.gati.listeners.OnImageClickListener;
import com.izyver.gati.listeners.OnImageDownloaded;
import com.izyver.gati.model.AppModel;
import com.izyver.gati.model.DateManager;
import com.izyver.gati.model.Model;
import com.izyver.gati.model.ScheduleObject;
import com.izyver.gati.model.network.NetworkManager;
import com.izyver.gati.model.network.UpdateApp;
import com.izyver.gati.utils.GatiPermissions;
import com.izyver.gati.utils.GatiPreferences;
import com.izyver.gati.utils.StackFragment;
import com.izyver.gati.view.FragmentImagePreview;
import com.izyver.gati.view.cardview.CardFragment;
import com.izyver.gati.view.cardview.RecyclerCardAdapter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {

    public static final int FULL_SCHEDULE = 1;
    public static final int CORRESPONDENCE_SCHEDULE = 0;
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
    private boolean isFirstCreate = true;

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
        loadImage(type);
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
        stopThread();
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

    @UiThread
    private void loadImage(final int type) {
        Runnable downloadImage = () -> {
            try {
                ScheduleObject localSchedule = model.getLocalSchedule();
                loadImageFromDb(type, getScheduleList(type, localSchedule));

                downloadImages(type);
            } catch (IOException | ParseException e) {
                    Log.e(this.getClass().getName(), "loadImage: ", e);
            }
        };

        stopThread();
        backgroundThread = new Thread(downloadImage);
        backgroundThread.start();
    }

    private void downloadImages(int type) throws IOException, ParseException {
        if (isInternetAvailable()) {
            ScheduleObject schedulers = model.getExistingSchedule();
            downloadImage(getScheduleList(type, schedulers));
        }
    }

    private void downloadImage(List<ScheduleObject.Schedule> schedulers) throws IOException, ParseException {
        for (ScheduleObject.Schedule schedule : schedulers) {
//            int index = date.getDayOfWeek(schedule);
//            if (isImageExist(type, index)) continue;
            model.downloadImage(schedule, downloadListener);
        }
    }

    private void loadImageFromDb(int type, List<ScheduleObject.Schedule> schedulers) throws ParseException {
        for (ScheduleObject.Schedule schedule : schedulers) {
            int index = date.getDayOfWeek(schedule);
            if (isImageExist(type, index)) continue;
            model.loadImage(schedule, downloadListener);
        }
    }

    private boolean isImageExist(int type, int index) {
        return (index < 0 || index >= 5) && (getScheduleList(type) != null && getScheduleList(type).get(index) != null);
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

    private void stopThread() {
        if (backgroundThread == null) return;
        backgroundThread.interrupt();
        backgroundThread = null;
    }

    private boolean isInternetAvailable() {
        if (context == null) return false;
        if (!network.isNetworkAvailable(context)) {
            showToast(R.string.network_is_not_available);
            return false;
        }
        if (!network.isInternetAvailable()){
            showToast(R.string.internet_is_not_available);
            return false;
        }
        return true;
    }

    private void showToast(int id) {
        handlerUI.post(() -> Toast.makeText(context, id, Toast.LENGTH_LONG).show());
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

    private List<ScheduleObject.Schedule> getScheduleList(int type, ScheduleObject scheduleObject){
        if (type == FULL_SCHEDULE) {
            return scheduleObject.getDay();
        } else if (type == CORRESPONDENCE_SCHEDULE) {
            return scheduleObject.getZao();
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
        if (schedulers == null) return list;
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
        boolean isOld = !date.isScheduleAtThisWeek(schedule);
        if (schedule.getType() == Presenter.FULL_SCHEDULE) {
            fullTimeSchedule.set(index, image);
        } else if (schedule.getType() == Presenter.CORRESPONDENCE_SCHEDULE) {
            correspondenceSchedule.set(index, image);
        }
        handlerUI.post(() -> {
            if (recyclerAdapter == null) return;
            recyclerAdapter.updateItem(index);
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