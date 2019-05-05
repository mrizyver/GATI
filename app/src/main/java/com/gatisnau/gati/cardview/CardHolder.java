package com.gatisnau.gati.cardview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gatisnau.gati.FragmentActivity;
import com.gatisnau.gati.OnImageClickListener;
import com.gatisnau.gati.R;

final class CardHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "CardHolder";
    private static final int TIME_LONG_CLICK = 1000;

    CardHolder(@NonNull View itemView) {
        super(itemView);
    }


    /* ----------interface---------- */

    public final void bind(final Bitmap bitmap, Context context, OnImageClickListener imageClickListener) {
        TextView tittle = findTitle();
        tittle.setText(getTitleId(getAdapterPosition()));
        if (bitmap != null) {
            setImage(bitmap, context, imageClickListener);
        }
    }


    /* ----------internal logic---------- */

    private Point getScreenSize(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImage(Bitmap bitmap, Context context, OnImageClickListener imageClickListener) {
        ImageView imageView = findImage();
        ((FragmentActivity) context).registerForContextMenu(imageView);

        final int viewWidth = getScreenSize(context).x;
        final float imageWidth = bitmap.getWidth();
        final float imageHeight = bitmap.getHeight();
        final float ratio = viewWidth / imageWidth;

        final int newImageHeight = (int) (imageHeight * ratio);
        imageView.setImageBitmap(resizeBitmap(bitmap, viewWidth, newImageHeight));

        imageView.setOnTouchListener(new View.OnTouchListener() {
            long firstTouchedTime = 0;
            float x = 0, y = 0;
            boolean isActionMoved = false;

            Handler handler = new Handler();
            Runnable startLongClick = () -> {
                if (context == null || !(context instanceof FragmentActivity)) return;
                ((FragmentActivity) context).onViewLongClick(imageView, getLayoutPosition(), x, y);
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isActionMoved = false;
                        firstTouchedTime = System.currentTimeMillis();
                        handler.postDelayed(startLongClick, TIME_LONG_CLICK);
                        x = event.getX();
                        y = event.getY();
                        Log.d(TAG, "onTouch: down");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "onTouch: move");
                        isActionMoved = true;
                        removeCallback();
                        return false;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "onTouch: up");
                        if (isActionMoved) return false;
                        long currentTime = System.currentTimeMillis();
                        if ((currentTime - firstTouchedTime) < TIME_LONG_CLICK) {
                            removeCallback();
                            imageClickListener.onImageClicked(bitmap);
                        }
                        return true;
                }
                return false;
            }

            private void removeCallback() {
                handler.removeCallbacks(startLongClick);
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

    private ImageView findImage() {
        return itemView.findViewById(R.id.card_image);
    }

    private TextView findTitle() {
        return itemView.findViewById(R.id.card_title);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}
