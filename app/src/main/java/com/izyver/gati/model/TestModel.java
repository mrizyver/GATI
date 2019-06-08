package com.izyver.gati.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.izyver.gati.listeners.OnImageDownloaded;
import com.izyver.gati.model.entity.ScheduleObject;
import com.izyver.gati.presenter.Presenter;
import com.izyver.gati.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestModel implements Model {

    private Context context;

    private ScheduleObject scheduleObject;
    private Map<String, Bitmap> images;

    public TestModel(Context context) {
        this.context = context;
        scheduleObject = new ScheduleObject();
        images = new HashMap<>();
    }

    @Override
    public ScheduleObject getExistingSchedule() {
        List<ScheduleObject.Schedule> list = new ArrayList<>();

        for (int i = -7; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            list.add(generateSchedule(calendar, i));
        }
        return (ScheduleObject) list;
    }

    @Override
    public ScheduleObject getLocalSchedule() {
        return null;
    }

    @Override
    public void loadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadedListener) throws ParseException {

    }

    @Override
    public void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        downloadListener.itemDownloaded(images.get(schedule.getImage()), schedule);
    }


    //--------------------------internal logic--------------------------//

    private ScheduleObject.Schedule generateSchedule(Calendar calendar, int id) {
        ScheduleObject.Schedule schedule = scheduleObject.new Schedule();
        SimpleDateFormat format = new SimpleDateFormat(ApplicationData.PATTERN_DATE);

        String formattedDate = format.format(calendar.getTime());
        schedule.setDate(formattedDate);
        schedule.setId(id);
        schedule.setImage(formattedDate);
        if (new Random().nextInt()%2 == 0){
            schedule.setType(Presenter.FULL_SCHEDULE);
        }else {
            schedule.setType(Presenter.CORRESPONDENCE_SCHEDULE);
        }
        images.put(formattedDate, createBitmap(calendar.get(Calendar.DAY_OF_WEEK)));
        return schedule;
    }

    private Bitmap createBitmap(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                return getBitmap(R.drawable.mon);
            case Calendar.TUESDAY:
                return getBitmap(R.drawable.tue);
            case Calendar.WEDNESDAY:
                return getBitmap(R.drawable.wed);
            case Calendar.THURSDAY:
                return getBitmap(R.drawable.thu);
            case Calendar.FRIDAY:
                return getBitmap(R.drawable.fri);
            case Calendar.SATURDAY:
                return getBitmap(R.drawable.sat);
            case Calendar.SUNDAY:
                return getBitmap(R.drawable.sun);
        }
        return null;
    }

    private Bitmap getBitmap(int id) {
        try {
            return Picasso.get().load(id).get();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
