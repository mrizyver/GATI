package com.izyver.gati.bussines.schedule.usecases

import java.util.*

interface IScheduleDateUseCase {

    fun getCurrentDate(): Date
    fun isScheduleDateActual(date: Date): Boolean

}