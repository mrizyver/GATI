package com.izyver.gati.data.network

import com.izyver.gati.data.android.GatiApi
import com.izyver.gati.data.network.models.ScheduleNetworkDto
import com.izyver.gati.extentions.convert

class RemoteDaytimeSource(private val gatiApi: GatiApi) : RemoteScheduleSource() {
    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        return gatiApi.getSchedulers().day
    }
}