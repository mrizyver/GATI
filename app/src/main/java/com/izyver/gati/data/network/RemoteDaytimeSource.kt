package com.izyver.gati.data.network

import android.graphics.Bitmap

class RemoteDaytimeSource: IRemoteScheduleDataSource {
    override suspend fun getBitmapBy(schedule: ScheduleNetworkDto): Bitmap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}