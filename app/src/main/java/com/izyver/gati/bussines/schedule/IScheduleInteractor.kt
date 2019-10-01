package com.izyver.gati.bussines.schedule

import com.izyver.gati.bussines.models.ScheduleImageDto
import kotlinx.coroutines.channels.Channel

interface IScheduleInteractor {

   fun loadNetworkImages(): Channel<ScheduleImageDto>

   fun cacheImagesExist(): Boolean
   fun loadCacheImage(): List<ScheduleImageDto>

   fun storageImagesExist(): Boolean
   fun loadStorageImage(): Channel<ScheduleImageDto>
}