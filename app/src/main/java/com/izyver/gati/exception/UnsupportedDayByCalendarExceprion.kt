package com.izyver.gati.exception

import java.lang.RuntimeException
import java.util.*

class UnsupportedDayByCalendarExceprion(calendar: Calendar)
    : RuntimeException("Current day of week by $calendar can not be convert to enum day")