package com.izyver.gati.exception

import com.izyver.gati.data.network.ScheduleType
import java.lang.RuntimeException

class UnsoppertedScheduleTypeException(scheduleType: ScheduleType)
    : RuntimeException("Unsupported schedule type exception ${scheduleType.name}")