package com.gatisnau.gati.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ImagesDAO {

    @Query("SELECT * FROM images WHERE `key` = :imageKey")
    ImageEntity getEntityByKey(String imageKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putImageEntity(ImageEntity imageEntity);
}
