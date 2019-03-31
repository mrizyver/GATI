package com.gatisnau.gati;

import android.nfc.FormatException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class DateManager {

    private static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss"; //2019-03-12 00:00:00


    private SimpleDateFormat simpleDateFormat;

    public DateManager() {
        simpleDateFormat = new SimpleDateFormat(PATTERN_DATE);
    }

    public int getDayOfWeek(ScheduleObject.Schedule schedule) {
        try {
            Date date = simpleDateFormat.parse(schedule.getDate());

            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setGregorianChange(date);
            return gregorianCalendar.get(Calendar.DAY_OF_WEEK);

        } catch (ParseException e) {
            e.printStackTrace();
           return -1;
        }
    }

    public int getCurrentDay(){
        long dateInSecond = System.currentTimeMillis() / 1000;

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setGregorianChange(new Date(dateInSecond));
        return gregorianCalendar.get(Calendar.DAY_OF_MONTH);
    }
}
