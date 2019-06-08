package com.izyver.gati.model;

import com.izyver.gati.listeners.OnImageDownloaded;
import com.izyver.gati.model.entity.ScheduleObject;

import java.io.IOException;
import java.text.ParseException;

public interface Model {

    ScheduleObject getExistingSchedule() throws IOException;

    ScheduleObject getLocalSchedule();

    void loadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadedListener) throws ParseException;

    void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException;
}
