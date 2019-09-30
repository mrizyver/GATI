package com.izyver.gati.bussines.models

import com.izyver.gati.bussines.models.Days.*
import com.izyver.gati.exception.UnsupportedIndexDayException
import org.junit.Assert.*
import org.junit.Test

class DaysTest{

    @Test
    fun days_index_test(){
        assertEquals(0, MONDAY.index)
        assertEquals(1, TUESDAY.index)
        assertEquals(2, WEDNESDAY.index)
        assertEquals(3, THURSDAY.index)
        assertEquals(4, FRIDAY.index)
        assertEquals(5, SATURDAY.index)
        assertEquals(6, SUNDAY.index)
    }

    @Test
    fun days_from_index_test(){
        assertEquals(MONDAY, Days.from(0))
        assertEquals(TUESDAY, Days.from(1))
        assertEquals(WEDNESDAY, Days.from(2))
        assertEquals(THURSDAY, Days.from(3))
        assertEquals(FRIDAY, Days.from(4))
        assertEquals(SATURDAY, Days.from(5))
        assertEquals(SUNDAY, Days.from(6))

        assertThrows(UnsupportedIndexDayException::class.java) { Days.from(7) }
    }
}