package com.izyver.gati.data.database

import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap

class LocalDistanceSource : ILocalScheduleDataSource{
    override fun getScheduleDescription(): ArrayList<ScheduleDbDtoWithoutBitmap> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStoredSchedule(): ArrayList<ScheduleDbDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCachedSchedules(): ArrayList<ScheduleDbDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getScheduleByDate(date: String?): ScheduleDbDto? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}