package com.izyver.gati.presentation.schedule.models

import android.graphics.Bitmap
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleState

data class ScheduleImageUI(
        val image: Bitmap?,
        val day: Days,
        val state: ScheduleState
)