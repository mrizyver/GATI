package com.izyver.gati.data.android

import com.izyver.gati.old.model.entity.ScheduleObject

import retrofit2.Call
import retrofit2.http.GET

interface GatiApi {
    @GET("api/schedule")
    fun getSchedulers(): Call<ScheduleObject>
}
