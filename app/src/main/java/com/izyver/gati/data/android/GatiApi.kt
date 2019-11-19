package com.izyver.gati.data.android


import com.izyver.gati.data.network.models.ScheduleObject
import retrofit2.http.GET

interface GatiApi {
    @GET("api/schedule")
    suspend fun getSchedulers(): ScheduleObject
}
