package com.gatisnau.gati.listeners;

import android.graphics.Bitmap;

import com.gatisnau.gati.model.ScheduleObject;

import java.text.ParseException;

public interface OnImageDownloaded {
    void itemDownloaded(Bitmap image, ScheduleObject.Schedule schedule) throws ParseException;
}
