package com.gatisnau.gati.model;

import com.gatisnau.gati.listeners.OnImageDownloaded;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface Model {

    ScheduleObject getExistingSchedule() throws IOException;

    ScheduleObject getLocalSchedule();

    void loadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadedListener) throws ParseException;

    void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException;
}
