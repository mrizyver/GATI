package com.izyver.gati.presentation.schedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izyver.gati.bussines.models.Days.*
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.bussines.models.ScheduleState.*
import com.izyver.gati.bussines.schedule.IScheduleInteractor
import com.izyver.gati.presentation.schedule.models.ScheduleImageUI
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.izyver.gati.bussines.models.Days.Companion.from


class ScheduleViewModel(private val scheduleInteractor: IScheduleInteractor) : ViewModel() {

    private val _scheduleImages: MutableLiveData<List<ScheduleImageUI>> = MutableLiveData(mockSchedules())
    val scheduleImage: LiveData<List<ScheduleImageUI>> = _scheduleImages

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var networkLoading: Job? = null

    fun loadImages() {
        viewModelScope.launch {
            loadImagesFromCache()
        }
        networkLoading = viewModelScope.async {
            loadImagesFromNetwork()
        }
    }

    fun reloadImages() {
        networkLoading?.cancel()
        networkLoading = viewModelScope.async {
            loadImagesFromNetwork()
        }
    }

    suspend fun getImageForShare(indexOfList: Int): Bitmap? {
        val imageByteArray: ByteArray = scheduleInteractor.getSchedule(from(indexOfList))
                ?: return null
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    private suspend fun loadImagesFromCache() {
        Log.d(this.javaClass.simpleName, "cache")
        takeSchedulesFromChanel(scheduleInteractor.loadCacheImage())
    }

    private suspend fun loadImagesFromNetwork() {
        _isLoading.postValue(true)
        Log.d(this.javaClass.simpleName, "network")
        takeSchedulesFromChanel(scheduleInteractor.loadNetworkImages())
        _isLoading.postValue(false)
    }

    private suspend fun takeSchedulesFromChanel(localChannel: Channel<ScheduleImageDto>) {
        for (scheduleImageDto in localChannel) {
            postDtoSchedule(scheduleImageDto)
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
        _scheduleImages.value = schedules
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