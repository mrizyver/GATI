package com.izyver.gati.data.network

import android.graphics.Bitmap
import com.izyver.gati.data.android.GatiApi
import com.izyver.gati.data.network.models.ScheduleNetworkDto

class RemoteDaytimeSource(private val gatiApi: GatiApi): IRemoteScheduleDataSource {

    override suspend fun getBitmapBy(schedule: ScheduleNetworkDto): Bitmap? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}