package com.izyver.gati.old.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ImageEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ImagesDAO imageDao();
}
