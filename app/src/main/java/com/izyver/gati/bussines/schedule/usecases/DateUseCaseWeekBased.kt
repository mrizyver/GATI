package com.izyver.gati.bussines.schedule.usecases

import java.util.*

open class DateUseCaseWeekBased : IScheduleDateUseCase {

    override fun getCurrentDate(): Date {
        return Date()
    }

    /**
     * @return true if date of schedule is in range by current week
     */
    override fun isScheduleDateActual(date: Date): Boolean {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = getCurrentDate()

        val calendarOfSchedule = Calendar.getInstance()
        calendarOfSchedule.time = date

        val dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK)

        currentCalendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 1))
        val afterFirstDayOfWeek = calendarOfSchedule.after(currentCalendar)
        currentCalendar.add(Calendar.DAY_OF_MONTH, 6 - (dayOfWeek - 1) + (dayOfWeek - 1))
        val beforeLastDayOfWeek = calendarOfSchedule.before(currentCalendar)

        return afterFirstDayOfWeek && beforeLastDayOfWeek
    }
}