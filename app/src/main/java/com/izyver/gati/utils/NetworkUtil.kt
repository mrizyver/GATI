package com.izyver.gati.utils

import com.izyver.gati.bussines.BASE_URL
import com.izyver.gati.bussines.PATH_TO_SCHEDULE
import com.izyver.gati.bussines.PREFIX

fun buildUrlForImage(imageName: String): String = "$PREFIX$BASE_URL$PATH_TO_SCHEDULE$imageName.png"
