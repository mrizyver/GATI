package com.izyver.gati.exception

import java.lang.RuntimeException

class DateParseException(date: String?)
    :RuntimeException("Cannot parse date. Invalid date format '$date'")
