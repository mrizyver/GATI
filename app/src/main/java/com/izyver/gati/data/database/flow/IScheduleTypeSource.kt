package com.izyver.gati.data.database.flow

import com.izyver.gati.bussines.models.ScheduleType

interface IScheduleTypeSource {
    fun saveScheduleType(type: ScheduleType)
    fun getScheduleType(): ScheduleType
}