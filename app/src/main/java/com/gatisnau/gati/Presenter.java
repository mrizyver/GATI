package com.gatisnau.gati;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.gatisnau.gati.cardview.RecyclerCardAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private Context context;
    private Handler handlerUI;
    private Thread backgroundThread;
    private Model model;
    private RecyclerCardAdapter recyclerAdapter;
    private DateManager date;

    private ArrayList<Bitmap> fullTimeSchedule;
    private Bitmap[] correspondenceSchedule = new Bitmap[6];

    public Presenter(Context context) {
        this.context = context;
        model = new Model();
        date = new DateManager();
        handlerUI = new Handler();
        fullTimeSchedule = new ArrayList<>(5);
    }

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        this.recyclerAdapter = adapter;
        adapter.setSchedulers(fullTimeSchedule);
    }

    class LifecycleListener implements GenericLifecycleObserver {
        @Override
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            switch (event){
                case ON_CREATE:
                    downloadImage();
            }
        }
    }

    private OnImageDownloaded downloadListener = (image, schedule) -> {
        int index = date.getDayOfWeek(schedule) - 2;
        fullTimeSchedule.add(image);
        handlerUI.post(()->{
           if (recyclerAdapter == null) return;
           recyclerAdapter.notifyDataSetChanged();
        });
    };

    private void downloadImage() {
        backgroundThread = new Thread(()-> {
            try {
                List<ScheduleObject.Schedule> schedulers = model.getExistingSchedule();
                for (ScheduleObject.Schedule schedule : schedulers) {
                    model.downloadImage(schedule, downloadListener);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        backgroundThread.start();
    }

    interface OnImageDownloaded {
        void itemDownloaded(Bitmap image, ScheduleObject.Schedule schedule);
    }
}