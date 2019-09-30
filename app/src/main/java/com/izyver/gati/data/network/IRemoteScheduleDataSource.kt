package com.izyver.gati.data.network

import android.graphics.Bitmap

interface IRemoteScheduleDataSource {

    suspend fun getExistingSchedule(): List<ScheduleNetworkDto>

    suspend fun getBitmapBy(schedule: ScheduleNetworkDto): Bitmap
}