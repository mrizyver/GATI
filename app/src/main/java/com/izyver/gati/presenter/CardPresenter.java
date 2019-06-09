package com.izyver.gati.presenter;

import android.annotation.SuppressLint;
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
import com.izyver.gati.utils.GatiPermissions;
import com.izyver.gati.view.cardview.CardView;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.izyver.gati.model.ApplicationData.FULL_SCHEDULE;
import static com.izyver.gati.presenter.PresenterActivity.REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE;
import static com.izyver.gati.utils.Util.getScreenSize;

public abstract class CardPresenter {

    public static final int COLOR_CODE_OLD_IMAGE = 0xFFFFFE;
    public static final int COLOR_CODE_NEW_IMAGE = 0xFFFFFF;

    public static final int MESSAGE_DOWNLOAD_IMAGE = 89;
    public static final int MESSAGE_RESIZE_IMAGE = 701;

    private static String TAG = "CardPresenter";
    protected Model model;
    protected DateManager date;
    protected Map<Integer, CardImage> schedulers;
    protected CardView view;
    private Handler uiHandler;

    @SuppressLint("UseSparseArrays")
    public CardPresenter() {
        schedulers = new HashMap<>(5);
        date = new DateManager();
        uiHandler = new Handler();
        this.model = new AppModel();
        downloadImage();
    }

    public static String getKey(int type) {
        return TAG + type;
    }

    public static CardPresenter getInstance(int scheduleType) {
        return scheduleType == FULL_SCHEDULE ? new CardPresenterFull() : new CardPresenterCorrespondence();
    }

    public abstract void downloadImage();

    public void shareImage(int index) {
        if (schedulers != null && view != null) {
            shareImage(schedulers.get(index).image, view);
        }
    }

    public void attachView(CardView cardView) {
        view = cardView;
    }

    public void detachView() {
        view = null;
    }

    public void shareFailure() {
        if (isNull(view)) return;
        Toast.makeText(view.getContext(), R.string.share_is_failure, Toast.LENGTH_LONG).show();
    }

    public Bitmap getSchedule(int index) {
        return schedulers.get(index).image;
    }


    protected void processPreviewImage(Map<Integer, CardImage> bitmaps) {
        for (Integer i : bitmaps.keySet()) {
            if (isNull(view)) break;
            onItemDownloaded(resizeBitmap(bitmaps.get(i).image, view.getContext()), bitmaps.get(i).isOld, i);
        }
    }

    protected Bitmap resizeBitmap(Bitmap bitmap, Context context) {
        final int viewWidth = getScreenSize(context).x;
        final float imageWidth = bitmap.getWidth();
        final float imageHeight = bitmap.getHeight();
        final float ratio = viewWidth / imageWidth;
        final int newImageHeight = (int) (imageHeight * ratio);
        return Bitmap.createScaledBitmap(bitmap, viewWidth, newImageHeight, false);
    }

    protected void onItemDownloaded(Bitmap smallBitmap, boolean isOld, int index) {
        if (view == null) return;
        uiHandler.post(() -> view.updateCard(smallBitmap, isOld, index));
    }

    /* ----------internal logic---------- */

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
    public void downloadImage() {
        Thread thread;
        Runnable downloadImage = () -> {
            ScheduleProcessing scheduleProcessing = new ScheduleProcessing();
            List<ImageEntity> localImageEntities = model.getLocalImages(FULL_SCHEDULE);
            Map<Integer, ImageEntity> actualLocal = scheduleProcessing.getActualImages(localImageEntities);
            for (Integer key : actualLocal.keySet()) {
                ImageEntity imageEntity = actualLocal.get(key);
                boolean isOld = date.isScheduleAtThisWeek(imageEntity);
                schedulers.put(key, new CardImage(imageEntity.getImageBitmap(), isOld));
            }
            processPreviewImage(schedulers);

            ScheduleObject schedule = null;
            try {
                schedule = model.getExistingSchedule();

                List<ScheduleObject.Schedule> scheduleDay = schedule.getDay();
                Map<Integer, ScheduleObject.Schedule> actualNetwork = scheduleProcessing.getActualImages(scheduleDay);

                for (Integer key : actualNetwork.keySet()) {
                    model.downloadImage(actualNetwork.get(key), (image, downloadedSchedule) -> {
                        boolean isOld = !date.isScheduleAtThisWeek(downloadedSchedule);
                        Bitmap previewImage = resizeBitmap(image, view.getContext());
                        onItemDownloaded(previewImage, isOld, key);
                        schedulers.put(key, new CardImage(image, isOld));
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };

        thread = new Thread(downloadImage);
        thread.start();
    }
}

class CardPresenterCorrespondence extends CardPresenter {

    @Override
    public void downloadImage() {

    }
}



