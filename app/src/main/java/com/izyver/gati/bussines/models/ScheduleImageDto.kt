package com.izyver.gati.bussines.models

import android.graphics.Bitmap
import java.util.*

data class ScheduleImageDto(
        val image: Bitmap?,
        val day: Days,
        val date: Date,
        val name: String,
        val isActual: Boolean
)