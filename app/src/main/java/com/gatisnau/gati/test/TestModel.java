package com.gatisnau.gati.test;

import com.gatisnau.gati.OnImageDownloaded;
import com.gatisnau.gati.ScheduleObject;
import com.gatisnau.gati.model.Model;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class TestModel implements Model {
    @Override
    public List<ScheduleObject.Schedule> getExistingSchedule() throws IOException {

        return null;
    }

    @Override
    public void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {

    }
}
