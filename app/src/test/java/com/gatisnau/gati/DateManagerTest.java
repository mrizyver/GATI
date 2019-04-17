package com.gatisnau.gati;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DateManagerTest {

    private ScheduleObject.Schedule schedule;
    private String patternString;
    private Date date = new Date();
    private Simp

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        schedule = new ScheduleObject().new Schedule();

        Class<DateManager> dataManagerClass = DateManager.class;
        Field patternField = dataManagerClass.getDeclaredField("PATTERN_DATE");
        patternField.setAccessible(true);
        patternString = (String) patternField.get(dataManagerClass.newInstance());

        SimpleDateFormat dateFormat = new SimpleDateFormat(patternString);
        String dataNow = dateFormat.format(date);

        schedule.setId(1);
        schedule.setDate(dataNow);
    }

    @Test
    public void isScheduleAtThisWeekTest() {
        DateManager dateManager = new DateManager();

        Assert.assertTrue(dateManager.isScheduleAtThisWeek(schedule));
    }
}