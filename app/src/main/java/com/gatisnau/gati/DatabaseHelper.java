package com.gatisnau.gati;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gatisnau.gati.Schedule.ImageDatabase.cols.DATE;
import static com.gatisnau.gati.Schedule.ImageDatabase.cols.DAY_OF_WEEK;
import static com.gatisnau.gati.Schedule.ImageDatabase.cols.IMAGE;
import static com.gatisnau.gati.Schedule.ImageDatabase.cols.SIZE;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imageDatabase.db";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Schedule.ImageDatabase.TABLE_NAME + "(" +
                DAY_OF_WEEK + " INTEGER, " +
                DATE + " BIGINT DEFAULT 0," +
                SIZE + " INTEGER DEFAULT 0, " +
                IMAGE + " BLOB DEFAULT NULL" +
                ");");
        ContentValues values = new ContentValues();
        int day = 2;
        while (day<8){
            values.put(DAY_OF_WEEK, day++);
            db.insert(Schedule.ImageDatabase.TABLE_NAME,null, values);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
