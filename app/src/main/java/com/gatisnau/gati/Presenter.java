package com.gatisnau.gati;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.gatisnau.gati.cardview.CardFragment;
import com.gatisnau.gati.cardview.RecyclerCardAdapter;
import com.gatisnau.gati.model.AppModel;
import com.gatisnau.gati.model.Model;
import com.gatisnau.gati.network.NetworkManager;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class Presenter {

    public static final int FULL_SCHEDULE = 1;
    public static final int CORRESPONDENCE_SCHEDULE = 2;

    private Context context;
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

    public Presenter(Context context) {
        this.context = context;
        model = new AppModel();
        date = new DateManager();
        network = new NetworkManager();
        handlerUI = new Handler();
        fullTimeSchedule = createBitmapList(5);
        correspondenceSchedule = createBitmapList(5);
        type = GatiPreferences.getTypeSchedule(context);
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

    public void startActivity(String packageActivity, String uri){
        if (context == null) return;
        Uri link = Uri.parse(uri);
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, link);
        activityIntent.setPackage(packageActivity);
        try{
            context.startActivity(activityIntent);
        }catch (ActivityNotFoundException e){
            context.startActivity(new Intent(Intent.ACTION_VIEW, link));
        }
    }

    public void setItem(int index){
        recyclerAdapter.toPosition(index);
    }

    /* ----------internal logic---------- */

    class ActivityLifecycleListener implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            stackFragment.replaceFragment(CardFragment.newInstance(), false);
            CalculateCurrentPosition();
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
    };
}