package com.izyver.gati.bussines.schedule

import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.data.schedule.ScheduleRepository
import kotlinx.coroutines.channels.Channel

class ScheduleInteractor(private val repository: ScheduleRepository) : IScheduleInteractor {

    override fun loadNetworkImages(): Channel<ScheduleImageDto> {
        return repository.loadNewImageFromNetwork()
    }

    override fun cacheImagesExist(): Boolean {
        return !repository.isCacheEmpty()
    }

    override fun loadCacheImage(): List<ScheduleImageDto> {
        return repository.getImageFromCache()
    }

    override fun storageImagesExist(): Boolean {
        return repository.isExistImagesInDB()
    }

    override fun loadStorageImage(): Channel<ScheduleImageDto>{
        return repository.loadImageFromStorage()
    }
}