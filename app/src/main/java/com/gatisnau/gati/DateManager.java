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
    private Date date;
    private SimpleDateFormat simpleDateFormat;

    @SuppressLint("SimpleDateFormat")
    public DateManager() {
        simpleDateFormat = new SimpleDateFormat(PATTERN_DATE);
        date = new Date();
    }


    /* ----------interface---------- */

    public int getDayOfWeek(ScheduleObject.Schedule schedule) {

        try {
            Calendar calendar = getDateOf(schedule);
            int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
            return day;
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
            Calendar oldCalendar = getDateOf(schedule);

            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTime(date);
            int dayOfWeek = newCalendar.get(Calendar.DAY_OF_WEEK);

            newCalendar.add(Calendar.DAY_OF_MONTH, - (dayOfWeek - 1));
            boolean afterFirstDayOfWeek = oldCalendar.after(newCalendar);
            newCalendar.add(Calendar.DAY_OF_MONTH, (6 - (dayOfWeek - 1) + (dayOfWeek - 1)));
            boolean beforeLastDayOfWeek = oldCalendar.before(newCalendar);

            return afterFirstDayOfWeek && beforeLastDayOfWeek;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private Calendar getDateOf(ScheduleObject.Schedule schedule) throws ParseException {
        Date date = simpleDateFormat.parse(schedule.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
