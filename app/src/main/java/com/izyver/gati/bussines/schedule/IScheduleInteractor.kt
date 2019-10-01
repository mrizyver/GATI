package com.izyver.gati.bussines.schedule

import com.izyver.gati.bussines.models.ScheduleImageDto
import kotlinx.coroutines.channels.Channel

interface IScheduleInteractor {

   fun loadNetworkImages(): Channel<ScheduleImageDto>

   fun loadCacheImage(): Channel<ScheduleImageDto>
}