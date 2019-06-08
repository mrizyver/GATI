package com.izyver.gati.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImagesDAO {

    @Query("SELECT * FROM images WHERE `date` = :date")
    ImageEntity getEntityByDate(String date);

    @Query("SELECT * FROM images WHERE `key` = :imageKey")
    ImageEntity getEntityByKey(String imageKey);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putImageEntity(ImageEntity imageEntity);

    @Query("SELECT * FROM images")
    List<ImageEntity> getAll();
}
