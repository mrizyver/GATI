package com.izyver.gati.bussines.models

import org.junit.Assert.*
import org.junit.Test

class DaysTest{

    @Test
    fun days_index_test(){
        assertEquals(0, Days.MONDAY.index)
        assertEquals(1, Days.TUESDAY.index)
        assertEquals(2, Days.WEDNESDAY.index)
        assertEquals(3, Days.THURSDAY.index)
        assertEquals(4, Days.FRIDAY.index)
        assertEquals(5, Days.SATURDAY.index)
        assertEquals(6, Days.SUNDAY.index)
    }
}