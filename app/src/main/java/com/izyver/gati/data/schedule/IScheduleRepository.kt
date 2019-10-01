package com.izyver.gati.data.schedule

import com.izyver.gati.bussines.models.ScheduleImageDto
import kotlinx.coroutines.channels.Channel

interface IScheduleRepository {

    fun loadNewImageFromNetwork(): Channel<ScheduleImageDto>

    fun isCacheEmpty(): Boolean
    fun getImageFromCache(): List<ScheduleImageDto>

    suspend fun isExistImagesInDB(): Boolean
    suspend fun loadImageFromStorage(): Channel<ScheduleImageDto>

}