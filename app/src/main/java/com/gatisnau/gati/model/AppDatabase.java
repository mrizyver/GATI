package com.gatisnau.gati.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gatisnau.gati.model.db.ImageEntity;
import com.gatisnau.gati.model.db.ImagesDAO;

@Database(entities = {ImageEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ImagesDAO imageDao();
}
