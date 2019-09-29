package com.izyver.gati.data.schedule

import com.izyver.gati.bussines.models.ScheduleImageDto
import kotlinx.coroutines.channels.Channel

class DistanceRepository : ScheduleRepository {
    override fun loadNewImageFromNetwork(): Channel<ScheduleImageDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isCacheEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getImageFromCache(): List<ScheduleImageDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isExistImagesInDB(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadImageFromStorage(): Channel<ScheduleImageDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}