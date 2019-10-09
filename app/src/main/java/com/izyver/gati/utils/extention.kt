package com.izyver.gati.utils

import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.schedule.models.ScheduleDbModel


fun MutableList<out ScheduleDbModel>.findByDay(day: Days): ScheduleDbModel?{
    for (scheduleDbModel in this) {
        if (Days.from(parseStandardGatiDate(scheduleDbModel.date) ?: continue) == day){
            return scheduleDbModel
        }
    }
    return null
}

fun  MutableList<ScheduleDbModel>.put(schedule: ScheduleDbModel){
    for ((i, _schedule) in this.withIndex()) {
        if (_schedule.dayWeek == schedule.dayWeek){
            this[i] = schedule
            return
        }
    }
    this.add(schedule)
}

fun MutableList<out ScheduleDbModel>.findByDate(date: String): ScheduleDbModel?{
    for (schedule in this) {
        if (schedule.date == date) {
            return schedule
        }
    }
    return null
}