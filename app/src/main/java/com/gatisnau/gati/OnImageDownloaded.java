package com.gatisnau.gati;

import android.graphics.Bitmap;

public interface OnImageDownloaded {
    void itemDownloaded(Bitmap image, ScheduleObject.Schedule schedule);
}
