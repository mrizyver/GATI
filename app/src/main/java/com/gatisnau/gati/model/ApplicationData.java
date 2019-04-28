package com.gatisnau.gati.model;

import android.app.Application;

import com.gatisnau.gati.network.GatiApi;
import com.gatisnau.gati.Presenter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationData extends Application {

    public static final String BASE_URL = "http://gatisnau.sumy.ua/";

    public static Presenter presenter;
    public static GatiApi gatiApi;

    @Override
    public void onCreate() {
        super.onCreate();
        presenter = new Presenter(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gatisnau.sumy.ua/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gatiApi = retrofit.create(GatiApi.class);
    }
}
