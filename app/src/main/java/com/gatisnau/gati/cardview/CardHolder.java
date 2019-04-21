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

import com.gatisnau.gati.OnImageClickListener;
import com.gatisnau.gati.R;

final class CardHolder extends RecyclerView.ViewHolder {

    private View cardItem;

    CardHolder(@NonNull View itemView) {
        super(itemView);
        this.cardItem = itemView;
    }


    /* ----------interface---------- */

    public final void bind(final Bitmap bitmap, Context context, OnImageClickListener imageClickListener) {
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

    private ImageView findImage() {
        return cardItem.findViewById(R.id.card_image);
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}
