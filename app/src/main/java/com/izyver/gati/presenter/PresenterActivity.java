package com.izyver.gati.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.izyver.gati.BuildConfig;
import com.izyver.gati.R;
import com.izyver.gati.model.AppModel;
import com.izyver.gati.model.DateManager;
import com.izyver.gati.model.Model;
import com.izyver.gati.model.entity.ScheduleObject;
import com.izyver.gati.network.NetworkManager;
import com.izyver.gati.network.UpdateApp;
import com.izyver.gati.utils.GatiPreferences;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.izyver.gati.model.ApplicationData.CORRESPONDENCE_SCHEDULE;
import static com.izyver.gati.model.ApplicationData.FULL_SCHEDULE;

public class PresenterActivity {

    public static final int REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE = 159;

    @Nullable private Context context;
    private Handler handlerUI;
    private Thread backgroundThread;
    private Model model;
    private DateManager date;
    private NetworkManager network;

    private int currentDay = 0;
    private int type;

    private ArrayList<Bitmap> correspondenceSchedule;

    public PresenterActivity() {
        this.model = new AppModel();
        this.date = new DateManager();
        this.network = new NetworkManager();
        this.handlerUI = new Handler();
        this.correspondenceSchedule = createBitmapList(5);
        this.type = GatiPreferences.getTypeSchedule(context);
    }


    /* ----------interface---------- */


    public void attachActivity(Activity activity) {
        this.context = activity;
    }

    /**
     * call this method when view is destroy
     */
    public void destroy() {
        context = null;
        stopThread();
    }



    public void updateApp() {
        new UpdateApp(context, handlerUI).startUpdate(BuildConfig.URL_VERSION_CONTROLL,BuildConfig.URL_UPDATE   );
    }

    /* ----------internal logic---------- */
//
//    public class ActivityLifecycleListener implements LifecycleObserver {
//        private UpdateApp update;
//        private int afterType = 0;
//
//        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//        public void onCreate() {
//            if (isFirstCreate) {
//                isFirstCreate = false;
//                update = new UpdateApp(context, handlerUI);
//                update.checkVersion(BuildConfig.URL_VERSION_CONTROLL);
//            }
//            stackFragment.replaceFragment(CardFragment.newInstance(), false);
//            CalculateCurrentPosition();
//        }
//
//        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//        public void onDestroy() {
//            if (update != null)
//                update.stop();
//            update = null;
//        }
//    }

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
//                ScheduleObject localSchedule = model.getLocalImage();
                loadImageFromDb(type, getScheduleList(type, null));

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
            model.downloadImage(schedule, null);
        }
    }

    private void loadImageFromDb(int type, List<ScheduleObject.Schedule> schedulers) throws ParseException {
        for (ScheduleObject.Schedule schedule : schedulers) {
            int index = date.getDayOfWeek(schedule);
            if (isImageExist(type, index)) continue;
//            model.loadImage(schedule, downloadListener);
        }
    }

    private boolean isImageExist(int type, int index) {
        return (index < 0 || index >= 5) && (getScheduleList(type) != null && getScheduleList(type).get(index) != null);
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
            return null;
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
//    private OnImageDownloaded downloadListener = (image, schedule) -> {
//        int index = date.getDayOfWeek(schedule);
//        boolean isOld = !date.isScheduleAtThisWeek(schedule);
//        if (schedule.getType() == Presenter.FULL_SCHEDULE) {
//            fullTimeSchedule.set(index, image);
//        } else if (schedule.getType() == Presenter.CORRESPONDENCE_SCHEDULE) {
//            correspondenceSchedule.set(index, image);
//        }
//        handlerUI.post(() -> {
//            if (recyclerAdapter == null) return;
//            recyclerAdapter.updateItem(index);
//            if (index == currentDay) {
//                recyclerAdapter.toPosition(currentDay);
//            }
//        });
//    };
}