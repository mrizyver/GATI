package com.izyver.gati.data.network

import android.graphics.Bitmap
import java.io.IOException

interface IRemoteScheduleDataSource {

    @Throws(IOException::class)
    suspend fun getExistingSchedule(): List<ScheduleNetworkDto>

    @Throws(IOException::class)
    suspend fun getBitmapBy(schedule: ScheduleNetworkDto): Bitmap
}