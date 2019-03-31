package com.gatisnau.gati.cardview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gatisnau.gati.R;

final class CardHolder extends RecyclerView.ViewHolder {

    private View cardItem;

    CardHolder(@NonNull View itemView) {
        super(itemView);
        this.cardItem = itemView;
    }

    public void bind(Bitmap bitmap, Context context) {
        ImageView imageView = cardItem.findViewById(R.id.card_image);

        int viewWidth = getScreenSize(context).x;
        float imageWidth = bitmap.getWidth();
        float imageHeight = bitmap.getHeight();
        float ratio = viewWidth / imageWidth;

        int newImageHeight = (int) (imageHeight * ratio);
        bitmap =  Bitmap.createScaledBitmap(bitmap, viewWidth, newImageHeight, false);
        imageView.setImageBitmap(bitmap);
    }

    private Point getScreenSize(Context context){
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
