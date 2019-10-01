package com.izyver.gati.data.database

import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap

interface ILocalScheduleDataSource {
    fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap>
    fun getSchedule(): List<ScheduleDbDto>

}