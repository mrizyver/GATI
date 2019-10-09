package com.izyver.gati.data.network

import com.izyver.gati.data.android.GatiApi
import com.izyver.gati.data.network.models.ScheduleNetworkDto

class RemoteDaytimeSource(private val gatiApi: GatiApi): IRemoteScheduleDataSource {

    override suspend fun download(schedule: ScheduleNetworkDto): ByteArray? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}