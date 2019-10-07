package com.izyver.gati.data.network

import com.izyver.gati.data.network.models.ScheduleNetworkDto
import java.io.IOException

interface IRemoteScheduleDataSource {

    @Throws(IOException::class)
    suspend fun getExistingSchedule(): List<ScheduleNetworkDto>

    @Throws(IOException::class)
    suspend fun download(schedule: ScheduleNetworkDto): ByteArray?
}