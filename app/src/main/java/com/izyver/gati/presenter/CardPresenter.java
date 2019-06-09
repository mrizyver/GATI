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
import com.izyver.gati.model.entity.ScheduleObject;
import com.izyver.gati.network.CardHandlerThread;
import com.izyver.gati.utils.GatiPermissions;
import com.izyver.gati.view.cardview.CardView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.izyver.gati.model.ApplicationData.FULL_SCHEDULE;
import static com.izyver.gati.presenter.PresenterActivity.REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE;
import static com.izyver.gati.utils.Util.getScreenSize;

public abstract class CardPresenter {

    public static final int MESSAGE_DOWNLOAD_IMAGE = 89;
    public static final int MESSAGE_RESIZE_IMAGE = 701;

    private static String TAG = "CardPresenter";
    protected Model model;
    protected DateManager date;
    protected CardHandlerThread cardHandle;
    protected ArrayList<Bitmap> schedulers;
    protected CardView view;
    private Handler uiHandler;

    public CardPresenter() {
        cardHandle = new CardHandlerThread(TAG);
        cardHandle.start();
        cardHandle.getLooper();
        schedulers = new ArrayList<>(5);
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
            shareImage(schedulers.get(index), view);
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
        return schedulers.get(index);
    }


    protected void processPreviewImage(List<Bitmap> bitmaps) {
        for (int i = 0; i < bitmaps.size(); i++) {
            if (isNull(view)) break;
            onItemDownloaded(resizeBitmap(bitmaps.get(i), view.getContext()), i);
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

    protected void onItemDownloaded(Bitmap smallBitmap, int index) {
        if (view == null) return;
        uiHandler.post(() -> view.updateCard(smallBitmap, index));
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
            List<ImageEntity> actualImages = scheduleProcessing.getActualImages(localImageEntities);
            for (ImageEntity entity : localImageEntities) {
                schedulers.add(entity.getImageBitmap());
            }
            processPreviewImage(schedulers);

            ScheduleObject schedule = null;
            try {
                schedule = model.getExistingSchedule();

                List<ScheduleObject.Schedule> scheduleDay = schedule.getDay();
                scheduleDay = scheduleProcessing.getActualImages(scheduleDay);

                for (ScheduleObject.Schedule day : scheduleDay) {
                    int index = date.getDayOfWeek(day);
                    model.downloadImage(day, (image, schedule1) -> {
                        onItemDownloaded(resizeBitmap(image, view.getContext()), index);
                        schedulers.add(image);
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



