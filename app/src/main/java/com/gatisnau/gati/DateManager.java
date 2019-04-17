package com.gatisnau.gati;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class DateManager {

    private static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss"; //2019-03-12 00:00:00

    private SimpleDateFormat simpleDateFormat;

    @SuppressLint("SimpleDateFormat")
    public DateManager() {
        simpleDateFormat = new SimpleDateFormat(PATTERN_DATE);
    }

    public int getDayOfWeek(ScheduleObject.Schedule schedule) {

        try {
            GregorianCalendar gregorianCalendar = getDateOf(schedule);
            return gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 2;
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

    public boolean isScheduleAtThisWeek(ScheduleObject.Schedule schedule){
        try {
            GregorianCalendar calendar = getDateOf(schedule);
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

            Date date = new Date();
            GregorianCalendar thisWeek = new GregorianCalendar();
            thisWeek.setGregorianChange(date);
            int currentWeekOfYear = thisWeek.get(Calendar.WEEK_OF_YEAR);

            return weekOfYear == currentWeekOfYear;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    private GregorianCalendar getDateOf(ScheduleObject.Schedule schedule) throws ParseException {
        Date date = simpleDateFormat.parse(schedule.getDate());
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setGregorianChange(date);
        return gregorianCalendar;
    }
}
