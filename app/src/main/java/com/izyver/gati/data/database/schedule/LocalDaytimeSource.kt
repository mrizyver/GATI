package com.izyver.gati.data.database.schedule

import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto
import com.izyver.gati.data.database.schedule.models.ScheduleDbDtoWithoutBitmap

class LocalDaytimeSource : ILocalScheduleDataSource {

    override fun getScheduleByDay(day: Days): ScheduleDbDto? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveSchedule(scheduleDbDto: ScheduleDbDto) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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