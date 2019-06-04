package com.izyver.gati;

import com.izyver.gati.model.ApplicationData;
import com.izyver.gati.model.DateManager;
import com.izyver.gati.model.ScheduleObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateManagerTest {

    private Date date = new Date();
    private SimpleDateFormat dateFormat;
    private GregorianCalendar calendar;

        @Before
        public void before() {
            String patternString = ApplicationData.PATTERN_DATE;
            dateFormat = new SimpleDateFormat(patternString);
            int month = Calendar.APRIL;
            int year = 2019;
            calendar = new GregorianCalendar();
            calendar.set(year, month, 17);
            date = calendar.getTime();
        }

    @Test
    public void isScheduleAtThisWeekTest() {
        DateManager dateManager = new DateManager();
        dateManager.setDate(date);

        setDay(calendar, 20);//saturday
        Assert.assertTrue(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 19);//friday
        Assert.assertTrue(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 18);//thursday
        Assert.assertTrue(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 17);//wednesday
        Assert.assertTrue(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 16);//tuesday
        Assert.assertTrue(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 15);//monday
        Assert.assertTrue(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 14);//saturday
        Assert.assertFalse(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 21);//sunday
        Assert.assertFalse(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 22);//monday
        Assert.assertFalse(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

        setDay(calendar, 23);//tuesday
        Assert.assertFalse(
                dateManager
                        .isScheduleAtThisWeek(
                                getSchedule(calendar)));

    }

    private void setDay(
            GregorianCalendar calendar,
            int day){
        calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    private ScheduleObject.Schedule getSchedule(
            GregorianCalendar calendar
    ) {
        ScheduleObject.Schedule schedule =
                new ScheduleObject().new Schedule();

        Date date = calendar.getTime();
        String dataNow = dateFormat
                .format(date);
        schedule.setDate(dataNow);
        schedule.setId(1);

        return schedule;
    }

}