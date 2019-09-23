package com.izyver.gati.old.model.entity;

import android.graphics.Bitmap;

public class CardImage {

    public CardImage(Bitmap image, boolean isOld) {
        this.image = image;
        this.isOld = isOld;
    }

    public Bitmap image;
    public boolean isOld;
}
