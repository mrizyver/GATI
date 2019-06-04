package com.izyver.gati.listeners;

import android.graphics.Bitmap;

import com.izyver.gati.model.ScheduleObject;

import java.text.ParseException;

public interface OnImageDownloaded {
    void itemDownloaded(Bitmap image, ScheduleObject.Schedule schedule) throws ParseException;
}
