package com.gatisnau.gati.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gatisnau.gati.model.db.ImageEntity;
import com.gatisnau.gati.model.network.GatiApi;
import com.gatisnau.gati.presenter.Presenter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationData extends Application {

    public static final String BASE_URL = "http://gatisnau.sumy.ua/";
    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss"; //2019-03-12 00:00:00

    public static Presenter presenter;
    public static GatiApi gatiApi;
    public static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        presenter = new Presenter(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gatisnau.sumy.ua/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gatiApi = retrofit.create(GatiApi.class);

        database = Room.databaseBuilder(this, AppDatabase.class, "gati_database").build();
    }
}
