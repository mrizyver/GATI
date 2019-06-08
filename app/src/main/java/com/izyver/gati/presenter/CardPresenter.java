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
import com.izyver.gati.utils.GatiPermissions;
import com.izyver.gati.view.cardview.CardView;

import java.util.ArrayList;

import static com.izyver.gati.model.ApplicationData.FULL_SCHEDULE;
import static com.izyver.gati.presenter.PresenterActivity.REQUEST_CODE_READE_WRITE_TO_SHARE_IMAGE;
import static com.izyver.gati.utils.Util.getScreenSize;

public abstract class CardPresenter {

    public static String TAG = "CardPresenter";
    private CardView view;
    private ArrayList<Bitmap> schedulers;
    private Handler uiHandler;

    public CardPresenter() {
        uiHandler = new Handler();
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
        if (nonNull(view))return;
        Toast.makeText(view.getContext(), R.string.share_is_failure, Toast.LENGTH_LONG).show();
    }

    public Bitmap getSchedule(int index) {
        return schedulers.get(index);
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
        uiHandler.post(() -> view.updateCard(smallBitmap, index));
    }

    /* ----------internal logic---------- */

    private void shareImage(Bitmap bitmap, CardView view) {
        if (nonNull(view)) return;
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

    private boolean nonNull(CardView view) {
        return view == null || view.getContext() == null;
    }
}

class CardPresenterFull extends CardPresenter {

    @Override
    public void downloadImage() {

    }
}

class CardPresenterCorrespondence extends CardPresenter {

    @Override
    public void downloadImage() {

    }
}



