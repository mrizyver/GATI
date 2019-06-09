package com.izyver.gati.model.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.izyver.gati.model.ComparableImage;

import java.io.ByteArrayOutputStream;

@Entity(tableName = "images")
public class ImageEntity implements ComparableImage {

    public ImageEntity() {
    }

    public ImageEntity(int id, String date, int type, Bitmap bitmap, String title, String dayWeek) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.image = getByteFromBitmap(bitmap);
        this.title = title;
        this.dayWeek = dayWeek;
        this.key = dayWeek + type;
    }

    @PrimaryKey
    @ColumnInfo(name = "key")
    @NonNull
    public String key = "key";

    @ColumnInfo(name = "image_id")
    public int id;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "type")
    public int type;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] image;


    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "day_week")
    public String dayWeek;


    public Bitmap getImageBitmap() {
        if (image == null) return null;
        Bitmap bitmap = getBitmap(image);
        return bitmap;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getDayOfWeek() {
        return dayWeek;
    }

    /*------------internal logic------------*/

    private Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private byte[] getByteFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }
}

