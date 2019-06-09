package com.izyver.gati.model;

import android.annotation.SuppressLint;

import com.izyver.gati.model.entity.ScheduleObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

 public class DateManager {

    private Date date;
    private SimpleDateFormat simpleDateFormat;

    @SuppressLint("SimpleDateFormat")
    public DateManager() {
        simpleDateFormat = new SimpleDateFormat(ApplicationData.PATTERN_DATE);
        date = new Date();
    }


    /* ----------interface---------- */

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDayOfWeek(ComparableImage comparableImage) {

        try {
            Calendar calendar = getDateOf(comparableImage);
            int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
            return day;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public boolean isCurrentDate(ComparableImage comparableImage) {
        try {
            Calendar calendar = getDateOf(comparableImage);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return day == getCurrentDay();
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    public int getCurrentDay(){
        long dateInSecond = System.currentTimeMillis() / 1000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateInSecond));
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK) - 2;
    }

    public boolean isScheduleAtThisWeek(ComparableImage comparableImage){
        try {
            Calendar oldCalendar = getDateOf(comparableImage);

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

    public Integer compare(ComparableImage first, ComparableImage second){
        Calendar firstDate;
        Calendar secondDate;
        try {
            firstDate = getDateOf(first);
            secondDate = getDateOf(second);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return firstDate.compareTo(secondDate);
    }


    //-----------------internal logic----------------//

    private Calendar getDateOf(ComparableImage comparableImage) throws ParseException {
        Date date = simpleDateFormat.parse(comparableImage.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
