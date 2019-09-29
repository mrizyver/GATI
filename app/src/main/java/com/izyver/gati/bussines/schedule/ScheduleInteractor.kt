package com.izyver.gati.bussines.schedule

import com.izyver.gati.data.ImageRepository

class ScheduleInteractor(private val repository: ImageRepository) : IScheduleInteractor {
    override suspend fun loadNetworkImages() {

    }

    override suspend fun cacheImagesExist(): Boolean {
        return false
    }

    override suspend fun loadCacheImage() {

    }

    override suspend fun storageImagesExist(): Boolean {
        return false
    }

    override suspend fun loadStorageImage() {

    }
}