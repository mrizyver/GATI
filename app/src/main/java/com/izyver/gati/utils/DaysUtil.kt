package com.izyver.gati.utils

import androidx.annotation.StringRes
import com.izyver.gati.R
import com.izyver.gati.bussines.models.Days

@StringRes
fun stringResBy(day: Days): Int {
    return when (day) {
        Days.MONDAY -> R.string.monday
        Days.TUESDAY -> R.string.thursday
        Days.WEDNESDAY -> R.string.wednesday
        Days.THURSDAY -> R.string.thursday
        Days.FRIDAY -> R.string.friday
        Days.SATURDAY -> R.string.saturday
        Days.SUNDAY -> R.string.sunday
    }
}