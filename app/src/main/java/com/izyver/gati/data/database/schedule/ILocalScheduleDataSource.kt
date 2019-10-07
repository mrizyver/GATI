package com.izyver.gati.data.database.schedule

import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto
import com.izyver.gati.data.database.schedule.models.ScheduleDbDtoWithoutBitmap

interface ILocalScheduleDataSource {
    fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap>
    fun getStoredSchedule(): List<ScheduleDbDto>
    fun getCachedSchedules(): List<ScheduleDbDto>
    fun getScheduleByDate(date: String?): ScheduleDbDto?
    fun saveSchedule(scheduleDbDto: ScheduleDbDto)
    fun getScheduleByDay(day: Days): ScheduleDbDto?
}