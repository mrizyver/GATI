package com.gatisnau.gati;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.gatisnau.gati.cardview.CardFragment;
import com.gatisnau.gati.cardview.RecyclerCardAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private Context context;
    private Handler handlerUI;
    private Thread backgroundThread;
    private Model model;
    private StackFragment stackFragment;
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

    /* ----------interface---------- */

    public void setFragmentManager(FragmentManager fragmentManager){
        this.stackFragment = new StackFragment(fragmentManager, R.id.fragment_container);
    }

    public void attachRecyclerAdapter(RecyclerCardAdapter adapter) {
        this.recyclerAdapter = adapter;
        adapter.setSchedulers(fullTimeSchedule);
        adapter.setImageClickListener(imageClickListener);
    }


    /* ----------internal logic---------- */

    class ActivityLifecycleListener implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            stackFragment.addFragment(CardFragment.newInstance());
            downloadImage();
        }
    }

    private void downloadImage() {
        backgroundThread = new Thread(() -> {
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


    /* ----------listeners---------- */

    private OnImageDownloaded downloadListener = (image, schedule) -> {
        fullTimeSchedule.add(image);
        handlerUI.post(() -> {
            if (recyclerAdapter == null) return;
            recyclerAdapter.notifyDataSetChanged();
        });
    };

    private OnImageClickListener imageClickListener= (bitmap) -> {
        FragmentImagePreview fragmentImage = FragmentImagePreview.newInstance();
        fragmentImage.setBitmap(bitmap);
        stackFragment.addToStackFragment(fragmentImage);
    };
}