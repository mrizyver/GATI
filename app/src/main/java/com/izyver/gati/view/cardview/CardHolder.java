package com.izyver.gati.view.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.izyver.gati.listeners.OnImageLongClickListener;
import com.izyver.gati.model.entity.CardImage;
import com.izyver.gati.presenter.CardPresenter;
import com.izyver.gati.view.FragmentActivity;
import com.izyver.gati.listeners.OnImageClickListener;
import com.izyver.gati.R;

import static com.izyver.gati.utils.Util.getScreenSize;

final class CardHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "CardHolder";
    private static float COLOR_TRANSPARENT_OLD_IMAGE = 0.55f;
    private static final int TIME_LONG_CLICK = 1000;

    CardHolder(@NonNull View itemView) {
        super(itemView);
    }


    /* ----------interface---------- */

    public final void bind(final CardImage cardImage, Context context, OnImageClickListener click, OnImageLongClickListener longClick) {
        TextView tittle = findTitle();
        tittle.setText(getTitleId(getAdapterPosition()));
        if (cardImage != null && cardImage.image != null) {
            itemView.setAlpha(1);
            setVisibleOldMarker(cardImage.isOld);
            setImage(cardImage.image, context, click, longClick);
            setVisibilityNotFountTextView(View.GONE);
        }else {
            itemView.setAlpha(COLOR_TRANSPARENT_OLD_IMAGE);
            setVisibleOldMarker(false);
            setVisibilityNotFountTextView(View.VISIBLE);
            findImage().setImageBitmap(null);
        }
    }

    private void setVisibleOldMarker(boolean isVisible) {
        int visible = isVisible ?View.VISIBLE : View.INVISIBLE;
        findOldMarker().setVisibility(visible);
    }


    /* ----------internal logic---------- */

    @SuppressLint("ClickableViewAccessibility")
    private void setImage(@NonNull Bitmap bitmap, Context context, OnImageClickListener imageClickListener, OnImageLongClickListener longClick) {
        ImageView imageView = findImage();
        ((FragmentActivity) context).registerForContextMenu(imageView);

        imageView.setImageBitmap(bitmap);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            long firstTouchedTime = 0;
            float x = 0, y = 0;
            boolean isActionMoved = false;

            Point screen = getScreenSize(context);

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isActionMoved = false;
                        firstTouchedTime = System.currentTimeMillis();
                        x = event.getX();
                        y = event.getY();
                        Log.d(TAG, "onTouch: down");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "onTouch: move");
                        float moveX = Math.abs(event.getX() - x);
                        float moveY = Math.abs(event.getY() - y);

                        if ((screen.x / 20) > moveX && (screen.y / 20) > moveY){
                            return true;
                        }

                        isActionMoved = true;
                        return false;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "onTouch: up");
                        if (isActionMoved) return false;
                        long currentTime = System.currentTimeMillis();
                        long diffTime = currentTime - firstTouchedTime;
                        if (diffTime < TIME_LONG_CLICK / 3) {
                            imageClickListener.onImageClicked(getAdapterPosition());
                        }else if (diffTime < TIME_LONG_CLICK){
                            longClick.onViewLongClick(imageView, getLayoutPosition(), x, y);
                            return true;
                        }else {
                            return false;
                        }
                }
                return false;
            }
        });


        System.gc();
    }

    private int getTitleId(int pos) {
        switch (pos) {
            case 0:
                return R.string.mon;
            case 1:
                return R.string.tue;
            case 2:
                return R.string.wed;
            case 3:
                return R.string.thu;
            case 4:
                return R.string.fri;
            case 5:
                return R.string.sat;
            default:
                return 0;
        }
    }

    private void setVisibilityNotFountTextView(int visible) {
        itemView.findViewById(R.id.tv_image_not_exist_found).setVisibility(visible);
    }

    private TextView findOldMarker(){
        return itemView.findViewById(R.id.marker_old_schedule);
    }

    private ImageView findImage() {
        return itemView.findViewById(R.id.card_image);
    }

    private TextView findTitle() {
        return itemView.findViewById(R.id.card_title);
    }
}
