package com.gatisnau.gati.network;

import com.gatisnau.gati.ScheduleObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GatiApi {

    @GET("api/schedule")
    Call<ScheduleObject> getSchedulers();
}
