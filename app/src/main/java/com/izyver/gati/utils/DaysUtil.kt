package com.izyver.gati.utils

import androidx.annotation.StringRes
import com.izyver.gati.R
import com.izyver.gati.bussines.models.Days
import java.text.SimpleDateFormat
import java.util.*

/**
 * Standard Gati date format
 */
const val DATE_PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss" //2019-03-12 00:00:00

@StringRes
fun stringResBy(day: Days): Int {
    return when (day) {
        Days.MONDAY -> R.string.monday
        Days.TUESDAY -> R.string.tuesday
        Days.WEDNESDAY -> R.string.wednesday
        Days.THURSDAY -> R.string.thursday
        Days.FRIDAY -> R.string.friday
        Days.SATURDAY -> R.string.saturday
        Days.SUNDAY -> R.string.sunday
    }
}

fun parseStandardGatiDate(strDate: String?): Date? {
    if (strDate == null) return null
    val simpleDateFormat = SimpleDateFormat(DATE_PATTERN_STANDARD)
    return simpleDateFormat.parse(strDate)
}

fun formatStandardGatiDate(date: Date?): String? {
    if (date == null) return null
    val simpleDateFormat = SimpleDateFormat(DATE_PATTERN_STANDARD)
    return simpleDateFormat.format(date)
}

