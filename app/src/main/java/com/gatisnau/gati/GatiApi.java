package com.gatisnau.gati;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GatiApi {

    @GET("api/schedule")
    Call<ScheduleObject> getSchedulers();
}
