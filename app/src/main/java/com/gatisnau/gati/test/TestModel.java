package com.gatisnau.gati.test;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Xml;
import android.widget.ImageView;

import com.gatisnau.gati.OnImageDownloaded;
import com.gatisnau.gati.R;
import com.gatisnau.gati.ScheduleObject;
import com.gatisnau.gati.model.ApplicationData;
import com.gatisnau.gati.model.Model;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
    public List<ScheduleObject.Schedule> getExistingSchedule() throws IOException {
        List<ScheduleObject.Schedule> list = new ArrayList<>();

        for (int i = -5; i < 5; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, i);
            list.add(generateSchedule(calendar, i));
        }
        return list;
    }

    @Override
    public void downloadImage(ScheduleObject.Schedule schedule, OnImageDownloaded downloadListener) throws IOException, ParseException {
        downloadListener.itemDownloaded(images.get(schedule.getImage()), schedule);
    }


    //--------------------------internal logic--------------------------//

    private ScheduleObject.Schedule generateSchedule(Calendar calendar, int id){
        ScheduleObject.Schedule schedule = scheduleObject.new Schedule();
        SimpleDateFormat format = new SimpleDateFormat(ApplicationData.PATTERN_DATE);

        String formattedDate = format.format(calendar.getTime());
        schedule.setDate(formattedDate);
        schedule.setId(id);
        schedule.setImage(formattedDate);
        images.put(formattedDate, createBitmap(calendar.get(Calendar.DAY_OF_WEEK)));
        return schedule;
    }

    private Bitmap createBitmap(int dayOfWeek) {
        switch (dayOfWeek){
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
            case  Calendar.SATURDAY:
                return getBitmap(R.drawable.sat);
            case Calendar.SUNDAY:
                return getBitmap(R.drawable.sun);
        }
        return null;
    }

    private Bitmap getBitmap(int id) {
        try {
            return Picasso.get().load(id).get();
        } catch (IOException|NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
