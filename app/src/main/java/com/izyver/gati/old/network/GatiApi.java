package com.izyver.gati.old.network;

import com.izyver.gati.old.model.entity.ScheduleObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GatiApi {

    @GET("api/schedule")
    Call<ScheduleObject> getSchedulers();
}
