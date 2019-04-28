package com.gatisnau.gati;

import android.annotation.SuppressLint;

import com.gatisnau.gati.model.ApplicationData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class DateManager {

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

    public boolean isCurrentDate(ScheduleObject.Schedule schedule) {
        try {
            Calendar calendar = getDateOf(schedule);
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



    //-----------------internal logic----------------//

    private Calendar getDateOf(ScheduleObject.Schedule schedule) throws ParseException {
        Date date = simpleDateFormat.parse(schedule.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
