package com.izyver.gati.data.network

import java.io.IOException

interface INetworkRepository {

    @Throws(IOException::class)
    fun getExistingSchedule(): List<ScheduleNetworkDto>

}