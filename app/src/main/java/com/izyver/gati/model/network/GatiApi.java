package com.izyver.gati.model.network;

import com.izyver.gati.model.ScheduleObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GatiApi {

    @GET("api/schedule")
    Call<ScheduleObject> getSchedulers();
}
