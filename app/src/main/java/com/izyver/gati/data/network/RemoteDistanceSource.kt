package com.izyver.gati.data.network

import com.izyver.gati.data.android.GatiApi
import com.izyver.gati.data.network.models.ScheduleNetworkDto

class RemoteDistanceSource(private val gatiApi: GatiApi) : RemoteScheduleSource() {
    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        return gatiApi.getSchedulers().zao
    }
}

