package com.izyver.gati.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.izyver.gati.model.db.ImageEntity;
import com.izyver.gati.model.db.ImagesDAO;

@Database(entities = {ImageEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ImagesDAO imageDao();
}
