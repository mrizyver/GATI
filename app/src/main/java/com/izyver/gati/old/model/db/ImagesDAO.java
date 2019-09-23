package com.izyver.gati.old.model.db;

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

    @Query("SELECT * FROM images WHERE `type` = :type")
    List<ImageEntity> getEntitiesByType(int type);

    @Query("SELECT * FROM images WHERE `key` IN (:imageKeys)")
    List<ImageEntity> getEntityByKeys(String[] imageKeys);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putImageEntity(ImageEntity imageEntity);

    @Query("SELECT * FROM images")
    List<ImageEntity> getAll();
}
