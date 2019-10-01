package com.izyver.gati.data.database

import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap

class LocalTestSource : ILocalScheduleDataSource{
    override fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSchedule(): List<ScheduleDbDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}