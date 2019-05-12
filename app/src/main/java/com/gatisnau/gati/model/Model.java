package com.gatisnau.gati.model;

import com.gatisnau.gati.listeners.OnImageDownloaded;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface Model {

    List<ScheduleObject.Schedule> getExistingSchedule() throws IOException;

    List<ScheduleObject.Schedule> getLocalSchedule();

    void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException;
}
