package com.izyver.gati.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.schedule.IScheduleInteractor
import com.izyver.gati.presentation.schedule.models.ScheduleImageUI
import kotlinx.coroutines.*

class ScheduleViewModel(private val scheduleInteractor: IScheduleInteractor) : ViewModel() {

    private val _scheduleImages: MutableLiveData<List<ScheduleImageUI>> = MutableLiveData(mockSchedules())
    private var imageLoadJob: Job? = null

    fun loadImages(): LiveData<List<ScheduleImageUI>>{
        imageLoadJob?.cancel()
        imageLoadJob = GlobalScope.async {
            loadImagesFromCache()
            loadImagesFromNetwork()
        }

        return _scheduleImages
    }

    private suspend fun loadImagesFromCache() {
        if (scheduleInteractor.cacheImagesExist()){
            scheduleInteractor.loadCacheImage()
        }else if (scheduleInteractor.storageImagesExist()){
            scheduleInteractor.loadStorageImage()
        }
    }

    private suspend fun loadImagesFromNetwork() {
        scheduleInteractor.loadNetworkImages()
    }

    private fun mockSchedules(): List<ScheduleImageUI> {
        return arrayListOf(
                ScheduleImageUI(null, Days.MONDAY),
                ScheduleImageUI(null, Days.TUESDAY),
                ScheduleImageUI(null, Days.WEDNESDAY),
                ScheduleImageUI(null, Days.THURSDAY),
                ScheduleImageUI(null, Days.FRIDAY)
        )
    }
}