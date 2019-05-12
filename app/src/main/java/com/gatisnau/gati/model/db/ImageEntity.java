package com.gatisnau.gati.model.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

@Entity(tableName = "images")
public class ImageEntity {

    ImageEntity() {
    }

    public ImageEntity(int id, String key, Bitmap bitmap) {
        this.id = id;
        this.key = key;
        this.image = getByteFromBitmap(bitmap);
    }

    @PrimaryKey
    @ColumnInfo(name = "image_id")
    public int id;

    @ColumnInfo(name = "key")
    public String key;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] image;

    public Bitmap getImageBitmap() {
        if (image == null) return null;
        Bitmap bitmap = getBitmap(image);
        return bitmap;
    }


    /*------------internal logic------------*/

    private Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private byte[] getByteFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }
}
