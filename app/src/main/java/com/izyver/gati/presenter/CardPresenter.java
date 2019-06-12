package com.izyver.gati.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.izyver.gati.R;
import com.izyver.gati.model.AppModel;
import com.izyver.gati.model.DateManager;
import com.izyver.gati.model.Model;
import com.izyver.gati.model.ScheduleProcessing;
import com.izyver.gati.model.db.ImageEntity;
import com.izyver.gati.model.entity.CardImage;
import com.izyver.gati.model.entity.ScheduleObject;
import com.izyver.gati.network.NetworkManager;
import com.izyver.gati.utils.GatiPermissions;
import com.izyver.gati.view.cardview.CardView;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.izyver.gati.model.ApplicationData.CORRESPONDENCE_SCHEDULE;
import static com.izyver.gati.model.ApplicationData.FULL_SCHEDULE;
import static com.izyver.gati.utils.Util.getScreenSize;

public abstract class CardPresenter {

    public static final int MESSAGE_DOWNLOAD_IMAGE = 89;
    public static final int MESSAGE_RESIZE_IMAGE = 701;
    public static final int REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE = 159;

    private static String TAG = "CardPresenter";
    protected Model model;
    private DateManager date;
    private Map<Integer, CardImage> schedulers;
    private Thread backgroundThread;
    private Thread loadThread;
    private CardView view;
    private Handler uiHandler;
    public int currentDay = 0;


    public CardPresenter() {
        schedulers = new ConcurrentHashMap<>(5);
        date = new DateManager();
        uiHandler = new Handler();
        this.model = new AppModel();

        calculateCurrentPosition();
        startBackgroundThread();
    }

    public static String getKey(int type) {
        return TAG + type;
    }

    public static CardPresenter getInstance(int scheduleType) {
        return scheduleType == FULL_SCHEDULE ? new CardPresenterFull() : new CardPresenterCorrespondence();
    }

    public abstract void downloadImage() throws IOException, ParseException;
    public abstract void loadImage();

    public void shareImage(int index) {
        if (schedulers != null && view != null) {
            shareImage(schedulers.get(index).image, view);
        }
    }

    public void attachView(CardView cardView) {
        view = cardView;

        stopThread(loadThread);
        loadThread = new Thread(() ->{
            isInternetAvailable();
            loadImage();
        });
        loadThread.start();

    }

    public void detachView() {
        view = null;
        stopThread(loadThread);
        loadThread = null;
    }

    public void shareFailure() {
        if (isNull(view)) return;
        Toast.makeText(view.getContext(), R.string.share_is_failure, Toast.LENGTH_LONG).show();
    }

    public Bitmap getSchedule(int index) {
        return schedulers.get(index).image;
    }


    protected void downloadSchedule(List<ScheduleObject.Schedule> scheduleDay) throws IOException, ParseException {
        ScheduleProcessing scheduleProcessing = new ScheduleProcessing();
        Map<Integer, ScheduleObject.Schedule> actualNetwork = scheduleProcessing.getActualImages(scheduleDay);

        for (Integer key : actualNetwork.keySet()) {
            model.downloadImage(actualNetwork.get(key), (image, downloadedSchedule) -> {
                if (isNull(view)) return;
                boolean isOld = !date.isScheduleAtThisWeek(downloadedSchedule);
                Bitmap previewImage = resizeBitmap(image, view.getContext());
                onItemDownloaded(previewImage, isOld, key);
                schedulers.put(key, new CardImage(image, isOld));
            });
        }
    }

    // TODO: 2019-06-09 Images need to be loaded from RAM, not from db
    protected void loadLocalSchedule(int type) {
        ScheduleProcessing scheduleProcessing = new ScheduleProcessing();
        List<ImageEntity> localImageEntities = model.getLocalImages(type);
        Map<Integer, ImageEntity> actualLocal = scheduleProcessing.getActualImages(localImageEntities);
        for (Integer key : actualLocal.keySet()) {
            if (Thread.interrupted()) return;
            ImageEntity imageEntity = actualLocal.get(key);
            boolean isOld = !date.isScheduleAtThisWeek(imageEntity);
            schedulers.put(key, new CardImage(imageEntity.getImageBitmap(), isOld));
        }
        processPreviewImage(schedulers);
    }


    /* ----------internal logic---------- */


    private void stopThread(Thread thread){
        if (thread!=null){
            thread.interrupt();
        }
    }

    private boolean isInternetAvailable() {
        if (isNull(view)) return false;
        NetworkManager network = new NetworkManager();
        if (!network.isNetworkAvailable(view.getContext())) {
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
        uiHandler.post(() -> {
            if (isNull(view)) return;
            Toast.makeText(view.getContext(), id, Toast.LENGTH_LONG).show();
        });
    }

    private void processPreviewImage(Map<Integer, CardImage> bitmaps) {
        for (Integer i : bitmaps.keySet()) {
            if (isNull(view)) break;
            onItemDownloaded(resizeBitmap(bitmaps.get(i).image, view.getContext()), bitmaps.get(i).isOld, i);
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, Context context) {
        final int viewWidth = getScreenSize(context).x;
        final float imageWidth = bitmap.getWidth();
        final float imageHeight = bitmap.getHeight();
        final float ratio = viewWidth / imageWidth;
        final int newImageHeight = (int) (imageHeight * ratio);
        return Bitmap.createScaledBitmap(bitmap, viewWidth, newImageHeight, false);
    }

    private synchronized void onItemDownloaded(Bitmap smallBitmap, boolean isOld, int index) {
        uiHandler.post(() -> {
            if (view == null) return;
            view.updateCard(smallBitmap, isOld, index);
        });
    }

    private void calculateCurrentPosition() {
        int currentDay = date.getDayOfWeek();
        if (currentDay >= 0 && currentDay < 6) {
            this.currentDay = currentDay;
        }
    }

    private void startBackgroundThread() {
        Runnable downloadImage = () -> {
            try {
                downloadImage();
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        };
        backgroundThread = new Thread(downloadImage, "full_presenter_thread");
        backgroundThread.start();

    }

    private void shareImage(Bitmap bitmap, CardView view) {
        if (isNull(view)) return;
        Context context = view.getContext();

        if (GatiPermissions.checkWritePermissions(context)) {
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Розклад", "розклад за якийсь день");
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            context.startActivity(intent);
        } else {
            GatiPermissions.requestReadWrite((Fragment) view, REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE);
        }
    }

    private boolean isNull(CardView view) {
        return view == null || view.getContext() == null;
    }
}

class CardPresenterFull extends CardPresenter {

    @Override
    public void downloadImage() throws IOException, ParseException {
        ScheduleObject schedule = model.getExistingSchedule();
        downloadSchedule(schedule.getDay());
    }

    @Override
    public void loadImage() {
        loadLocalSchedule(FULL_SCHEDULE);
    }
}

class CardPresenterCorrespondence extends CardPresenter {

    @Override
    public void downloadImage() throws IOException, ParseException {
        ScheduleObject schedule = model.getExistingSchedule();
        downloadSchedule(schedule.getZao());
    }

    @Override
    public void loadImage() {
        loadLocalSchedule(CORRESPONDENCE_SCHEDULE);
    }
}



