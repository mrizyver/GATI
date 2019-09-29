package com.izyver.gati.bussines.schedule

interface IScheduleInteractor {

    suspend fun loadNetworkImages()

    suspend fun cacheImagesExist(): Boolean
    suspend fun loadCacheImage()

    suspend fun storageImagesExist(): Boolean
    suspend fun loadStorageImage()
}