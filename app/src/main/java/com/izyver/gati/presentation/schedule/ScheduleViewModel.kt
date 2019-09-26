package com.izyver.gati.presentation.schedule

import androidx.lifecycle.ViewModel
import com.izyver.gati.bussines.schedule.IScheduleInteractor

class ScheduleViewModel(private val scheduleInteractor: IScheduleInteractor) : ViewModel() {

    companion object Type{
        const val DAYTIME = "daytime_scheduler"
        const val DISTANCE = "distance_scheduler"
    }
}