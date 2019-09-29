package com.izyver.gati.presentation.schedule.models

import android.graphics.Bitmap
import com.izyver.gati.bussines.models.Days

data class ScheduleImageUI(
        val image: Bitmap?,
        val day: Days)