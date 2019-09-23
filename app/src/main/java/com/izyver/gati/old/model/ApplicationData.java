package com.izyver.gati.old.model;

import android.app.Application;

import androidx.room.Room;

import com.izyver.gati.old.model.db.AppDatabase;
import com.izyver.gati.old.network.GatiApi;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationData extends Application {

    public static final String PREFIX = "http://";
    public static final String BASE_URL = "gati.snau.edu.ua";
    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss"; //2019-03-12 00:00:00
    public static final int FULL_SCHEDULE = 1;
    public static final int CORRESPONDENCE_SCHEDULE = 0;

    public static GatiApi gatiApi;
    public static AppDatabase database;
    public static Map cache;

    @Override
    public void onCreate() {
        super.onCreate();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PREFIX + BASE_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gatiApi = retrofit.create(GatiApi.class);

        database = Room.databaseBuilder(this, AppDatabase.class, "gati_database").build();
        cache = new HashMap();
    }
}
