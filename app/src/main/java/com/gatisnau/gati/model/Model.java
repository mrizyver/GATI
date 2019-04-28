package com.gatisnau.gati.model;

import com.gatisnau.gati.OnImageDownloaded;
import com.gatisnau.gati.ScheduleObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface Model {

    List<ScheduleObject.Schedule> getExistingSchedule() throws IOException;

    void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException;
}
