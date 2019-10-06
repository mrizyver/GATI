package com.izyver.gati.data.database

import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap

interface ILocalScheduleDataSource {
    fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap>
    fun getStoredSchedule(): List<ScheduleDbDto>
    fun getCachedSchedules(): List<ScheduleDbDto>
    fun getScheduleByDate(date: String?): ScheduleDbDto?
}