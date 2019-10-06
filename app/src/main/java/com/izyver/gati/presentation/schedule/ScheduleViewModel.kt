package com.izyver.gati.presentation.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izyver.gati.bussines.models.Days.*
import com.izyver.gati.bussines.models.ScheduleImageDto
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
        val localChannel = scheduleInteractor.loadCacheImage()
    }

    private suspend fun loadImagesFromNetwork() {
        val networkChanel = scheduleInteractor.loadNetworkImages()
        for (image in networkChanel) {
            postDtoSchedule(image)
        }
    }

    private fun postDtoSchedule(image: ScheduleImageDto) {
        val schedules = (_scheduleImages.value ?: mockSchedules()).toMutableList()
        val index = image.day.index
        val scheduleUI = ScheduleImageUI(
                image.image,
                image.day,
                if (image.isActual) ACTUAL else OLD)
        if (image.day != SUNDAY && image.day != SATURDAY) {
            schedules[index] = scheduleUI
        } else {
            schedules.add(scheduleUI)
        }
        _scheduleImages.postValue(schedules)
    }

    private fun mockSchedules(): MutableList<ScheduleImageUI> {
        return arrayListOf(
                ScheduleImageUI(null, MONDAY, ABSENT),
                ScheduleImageUI(null, TUESDAY, ABSENT),
                ScheduleImageUI(null, WEDNESDAY, ABSENT),
                ScheduleImageUI(null, THURSDAY, ABSENT),
                ScheduleImageUI(null, FRIDAY, ABSENT)
        )
    }
}