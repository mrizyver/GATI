package com.izyver.gati.bussines.schedule.usecases

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class DateUseCaseWeekBasedTest {

    private val dateUseCaseTest = object : DateUseCaseWeekBased() {
        override fun getCurrentDate(): Date {
            return getCalendar().time
        }
    }

    private var calendar: Calendar = getCalendar()

    @Test
    fun isScheduleDateActual() {

        setDay(calendar, 20)//saturday
        assertTrue(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 19)//friday
        assertTrue(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 18)//thursday
        assertTrue(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 17)//wednesday
        assertTrue(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 16)//tuesday
        assertTrue(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 15)//monday
        assertTrue(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 14)//saturday
        assertFalse(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 13)//sunday
        assertFalse(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 21)//sunday
        assertFalse(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 22)//monday
        assertFalse(dateUseCaseTest.isScheduleDateActual(calendar.time))

        setDay(calendar, 23)//tuesday
        assertFalse(dateUseCaseTest.isScheduleDateActual(calendar.time))
    }

    private fun setDay(
            calendar: Calendar,
            day: Int) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
    }

    private fun getCalendar(): Calendar {
        val month = Calendar.APRIL
        val year = 2019
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 17) //wednesday
        return calendar
    }
}