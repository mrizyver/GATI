package com.gatisnau.gati.cardview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gatisnau.gati.OnImageClickListener;
import com.gatisnau.gati.R;

final class CardHolder extends RecyclerView.ViewHolder {

    CardHolder(@NonNull View itemView) {
        super(itemView);
    }


    /* ----------interface---------- */

    public final void bind(final Bitmap bitmap, Context context, OnImageClickListener imageClickListener) {
        TextView tittle = findTitle();
        tittle.setText(getTitleId(getAdapterPosition()));
        if (bitmap != null){
            setImage(bitmap, context, imageClickListener);
        }
    }


    /* ----------logic---------- */

    private Point getScreenSize(Context context){
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void setImage(Bitmap bitmap, Context context, OnImageClickListener imageClickListener) {
        ImageView imageView = findImage();
        final int viewWidth = getScreenSize(context).x;
        final float imageWidth = bitmap.getWidth();
        final float imageHeight = bitmap.getHeight();
        final float ratio = viewWidth / imageWidth;

        final int newImageHeight = (int) (imageHeight * ratio);
        imageView.setImageBitmap(resizeBitmap(bitmap, viewWidth, newImageHeight));
        imageView.setOnClickListener(v -> imageClickListener.onImageClicked(bitmap));
        System.gc();
    }

    private int getTitleId(int pos) {
        switch (pos){
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
