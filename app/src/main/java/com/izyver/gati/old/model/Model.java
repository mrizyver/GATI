package com.izyver.gati.old.model;

import com.izyver.gati.old.listeners.OnImageDownloaded;
import com.izyver.gati.old.model.entity.ScheduleObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface Model {

    ScheduleObject getExistingSchedule() throws IOException;

    List getLocalImages(int type);

    void loadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadedListener) throws ParseException;

    void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException;
}
