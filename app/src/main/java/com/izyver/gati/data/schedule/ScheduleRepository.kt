package com.izyver.gati.data.schedule

import com.izyver.gati.bussines.models.ScheduleImageDto
import kotlinx.coroutines.channels.Channel

interface ScheduleRepository {

    fun loadNewImageFromNetwork(): Channel<ScheduleImageDto>

    fun isCacheEmpty(): Boolean
    fun getImageFromCache(): List<ScheduleImageDto>

    fun isExistImagesInDB(): Boolean
    fun loadImageFromStorage(): Channel<ScheduleImageDto>

}