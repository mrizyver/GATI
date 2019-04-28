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
import com.gatisnau.gati.model.AppModel;
import com.gatisnau.gati.network.NetworkManager;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private Context context;
    private Handler handlerUI;
    private Thread backgroundThread;
    private AppModel model;
    private StackFragment stackFragment;
    private RecyclerCardAdapter recyclerAdapter;
    private DateManager date;
    private NetworkManager network;

    private ArrayList<Bitmap> fullTimeSchedule;
    private Bitmap[] correspondenceSchedule = new Bitmap[6];

    public Presenter(Context context) {
        this.context = context;
        model = new AppModel();
        date = new DateManager();
        network = new NetworkManager();
        handlerUI = new Handler();
        fullTimeSchedule = createBitmapList(5);
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
        if (network.isNetworkAvailable(context) == false){
            return false;
        }
//        if (model.isInternetAvailable() == false){
//            return false;
//        }
        return true;
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
        int index = date.getDayOfWeek(schedule);
        if (index < 0) return;
        fullTimeSchedule.set(index, image);
        handlerUI.post(() -> {
            if (recyclerAdapter == null) return;
            recyclerAdapter.notifyItemChanged(index);
        });
    };

    private OnImageClickListener imageClickListener= (bitmap) -> {
        FragmentImagePreview fragmentImage = FragmentImagePreview.newInstance();
        fragmentImage.setBitmap(bitmap);
        stackFragment.addToStackFragment(fragmentImage);
    };
}