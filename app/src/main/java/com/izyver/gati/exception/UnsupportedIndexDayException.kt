package com.izyver.gati.exception

import java.lang.RuntimeException

class UnsupportedIndexDayException(index: Int)
    : RuntimeException("Day by index $index doesn't exist")