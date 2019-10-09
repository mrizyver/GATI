package com.izyver.gati.data.database.schedule

import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto
import com.izyver.gati.data.database.schedule.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.network.models.ScheduleNetworkDto

interface ILocalScheduleDataSource {
    fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap>
    fun getStoredSchedule(): List<ScheduleDbDto>
    fun getCachedSchedules(): List<ScheduleDbDto>
    fun getScheduleByDate(date: String?): ScheduleDbDto?
    fun saveSchedule(schedule: ScheduleNetworkDto, image: ByteArray?)
    fun getScheduleByDay(day: Days): ScheduleDbDto?
}