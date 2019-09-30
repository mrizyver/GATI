package com.izyver.gati.bussines.models

import com.izyver.gati.exception.UnsupportedDayByCalendarExceprion
import com.izyver.gati.exception.UnsupportedIndexDayException
import java.util.*

enum class Days(val index: Int) {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);

    companion object {
        fun from(index: Int): Days {
            return when (index) {
                0 -> MONDAY
                1 -> TUESDAY
                2 -> WEDNESDAY
                3 -> THURSDAY
                4 -> FRIDAY
                5 -> SATURDAY
                6 -> SUNDAY
                else -> throw UnsupportedIndexDayException(index)
            }
        }

        fun from(date: Date): Days{
            val calendar = Calendar.getInstance()
            calendar.time = date
            return from(calendar)
        }

        fun from(calendar: Calendar): Days{
            return when(calendar.get(Calendar.DAY_OF_WEEK)){
                Calendar.MONDAY -> MONDAY
                Calendar.TUESDAY -> TUESDAY
                Calendar.WEDNESDAY -> WEDNESDAY
                Calendar.THURSDAY -> THURSDAY
                Calendar.FRIDAY -> FRIDAY
                Calendar.SATURDAY -> SATURDAY
                Calendar.SUNDAY -> SUNDAY
                else -> throw UnsupportedDayByCalendarExceprion(calendar)
            }
        }
    }
}