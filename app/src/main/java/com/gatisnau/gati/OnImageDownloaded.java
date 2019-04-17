package com.gatisnau.gati;

import android.graphics.Bitmap;

import java.text.ParseException;

public interface OnImageDownloaded {
    void itemDownloaded(Bitmap image, ScheduleObject.Schedule schedule) throws ParseException;
}
