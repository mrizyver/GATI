package com.gatisnau.gati.cardview;

import android.graphics.Bitmap;

public class Presenter {
    private Bitmap[] fullTimeSchedule = new Bitmap[6];
    private Bitmap[] correspondenceSchedule = new Bitmap[6];

    public Bitmap getCorrespondenceAt(int index) {
        return correspondenceSchedule[index];
    }
}