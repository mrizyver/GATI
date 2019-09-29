package com.izyver.gati.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleState.*
import com.izyver.gati.bussines.schedule.IScheduleInteractor
import com.izyver.gati.presentation.schedule.models.ScheduleImageUI
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

class ScheduleViewModel(private val scheduleInteractor: IScheduleInteractor) : ViewModel() {

    private val _scheduleImages: MutableLiveData<List<ScheduleImageUI>> = MutableLiveData(mockSchedules())
    private var imageLoadJob: Job? = null

    fun loadImages(): LiveData<List<ScheduleImageUI>> {
        imageLoadJob?.cancel()
        imageLoadJob = GlobalScope.async {
            loadImagesFromCache()
            loadImagesFromNetwork()
        }
        return _scheduleImages
    }

    private suspend fun loadImagesFromCache() {
        if (scheduleInteractor.cacheImagesExist()) {
            scheduleInteractor.loadCacheImage()
        } else if (scheduleInteractor.storageImagesExist()) {
            scheduleInteractor.loadStorageImage()
        }
    }

    //TODO it is a business logic
    private suspend fun loadImagesFromNetwork() {
        val networkChanel = scheduleInteractor.loadNetworkImages()
        for (image in networkChanel) {
            val schedules = (_scheduleImages.value ?: mockSchedules()).toMutableList()
            val index = image.day.index
            val scheduleUI = ScheduleImageUI(
                    image.image,
                    image.day,
                    if (image.isActual) ACTUAL else OLD)
            if (index < schedules.size - 1) {
                schedules[index] = scheduleUI
            }else{
                schedules.add(scheduleUI)
            }
            _scheduleImages.postValue(schedules)
        }
    }

    private fun mockSchedules(): MutableList<ScheduleImageUI> {
        return arrayListOf(
                ScheduleImageUI(null, Days.MONDAY, ABSENT),
                ScheduleImageUI(null, Days.TUESDAY, ABSENT),
                ScheduleImageUI(null, Days.WEDNESDAY, ABSENT),
                ScheduleImageUI(null, Days.THURSDAY, ABSENT),
                ScheduleImageUI(null, Days.FRIDAY, ABSENT)
        )
    }
}