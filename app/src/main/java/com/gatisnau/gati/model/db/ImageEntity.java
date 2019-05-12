package com.gatisnau.gati.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class ImageEntity {

    @PrimaryKey
    @ColumnInfo(name = "image_id")
    int id;

    @ColumnInfo(name = "key")
    String key;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    byte[] image;
}
