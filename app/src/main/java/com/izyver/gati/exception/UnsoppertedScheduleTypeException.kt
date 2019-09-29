package com.izyver.gati.exception

import com.izyver.gati.bussines.models.ScheduleType
import java.lang.RuntimeException

class UnsoppertedScheduleTypeException(scheduleType: ScheduleType)
    : RuntimeException("Unsupported schedule type exception ${scheduleType.name}")