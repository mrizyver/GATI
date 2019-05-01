package com.gatisnau.gati;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.gatisnau.gati.cardview.CardFragment;
import com.gatisnau.gati.cardview.RecyclerCardAdapter;
import com.gatisnau.gati.model.Model;
import com.gatisnau.gati.network.NetworkManager;
import com.gatisnau.gati.test.TestModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {

    public static final int FULL_SCHEDULE = 160;
    public static final int CORRESPONDENCE_SCHEDULE = 281;

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
    private ArrayList<Bitmap>  correspondenceSchedule;

    public Presenter(Context context) {
        this.context = context;
        model = new TestModel(context);
        date = new DateManager();
        network = new NetworkManager();
        handlerUI = new Handler();
        fullTimeSchedule = createBitmapList(5);
        correspondenceSchedule = createBitmapList(5);
        type = GatiPreferences.getTypeSchedule(context);
    }


    /* ----------interface---------- */

    public void setFragmentManager(FragmentManager fragmentManager){
        this.stackFragment = new StackFragment(fragmentManager, R.id.fragment_container);
    }

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        this.recyclerAdapter = adapter;
        adapter.setSchedulers(getScheduleList(type));
        adapter.setImageClickListener(imageClickListener);
        adapter.toPosition(currentDay);
    }


    public void changeSchedule(int type) {
        this.type = type;
        if (type == FULL_SCHEDULE){
            stackFragment.setAnimation(R.animator.slide_in_right_start, R.animator.slide_in_right_end);
        }else {
            stackFragment.setAnimation(R.animator.slide_in_left_start, R.animator.slide_in_left_end);
        }
        stackFragment.replaceFragment(CardFragment.newInstance(), true);
    }


    /* ----------internal logic---------- */

    class ActivityLifecycleListener implements LifecycleObserver {
        private boolean isCreated = false;

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            stackFragment.replaceFragment(CardFragment.newInstance(), false);
            CalculateCurrentPosition();
            if (!isCreated){
                isCreated = true;
                downloadImage();
            }
        }
    }

    private void CalculateCurrentPosition() {
        int currentDay = date.getCurrentDayOfWeek();
        if (currentDay > 0 && currentDay < 6){
            this.currentDay = date.getCurrentDay();
        }
    }

    private void downloadImage() {
        backgroundThread = new Thread(() -> {
            try {
                if (checkInternetConnection() == false) return;

                List<ScheduleObject.Schedule> schedulers = model.getExistingSchedule();
                schedulers = getNeededSchedule(schedulers);
                for (ScheduleObject.Schedule schedule : schedulers) {
                    model.downloadImage(schedule, downloadListener);
                    System.out.println("start download " + schedule);
                }
            } catch (IOException|ParseException e) {
                Log.e(this.getClass().getName(), "downloadImage: ", e);
            }
        });
        backgroundThread.start();
    }

    // TODO: 4/21/19 alerts
    private boolean checkInternetConnection(){
        if (!network.isNetworkAvailable(context)){
            return false;
        }
//        if (model.isInternetAvailable() == false){
//            return false;
//        }
        return true;
    }

    private ArrayList<Bitmap> getScheduleList(int type) {
        if (type == FULL_SCHEDULE){
            return fullTimeSchedule;
        }else if (type == CORRESPONDENCE_SCHEDULE){
            return correspondenceSchedule;
        }else {
            return null;
        }
    }

    private List<ScheduleObject.Schedule> getNeededSchedule(List<ScheduleObject.Schedule> schedulers) {
        List<ScheduleObject.Schedule> list = getTheRight(schedulers, true);
        if (!list.isEmpty()){
            return list;
        }else {
            return getTheRight(schedulers, false);
        }
    }

    private List<ScheduleObject.Schedule> getTheRight(List<ScheduleObject.Schedule> schedulers, boolean isActual){
        List<ScheduleObject.Schedule> list = new ArrayList<>();
        if (isActual){
            for (ScheduleObject.Schedule schedule : schedulers) {
                if (!date.isScheduleAtThisWeek(schedule)) continue;
                if (date.isCurrentDate(schedule)) {
                    handlerUI.post(() -> recyclerAdapter.toPosition(fullTimeSchedule.indexOf(schedule)));
                }
                list.add(schedule);
            }
        }else {
            int size = schedulers.size() <= 5 ? schedulers.size() : 5;
            for (int i = 0; i < size; i++) {
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
        int index = date.getCurrentDayOfWeek(schedule);
        if (index < 0 || index >= 5) return;
        fullTimeSchedule.set(index, image);
        handlerUI.post(() -> {
            if (recyclerAdapter == null) return;
            recyclerAdapter.notifyItemChanged(index);
            if (index == currentDay){
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