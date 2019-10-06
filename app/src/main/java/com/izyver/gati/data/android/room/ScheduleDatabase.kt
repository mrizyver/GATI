package com.izyver.gati.data.android.room

import androidx.room.Database
import androidx.room.RoomDatabase

import com.izyver.gati.old.model.db.ImageEntity
import com.izyver.gati.old.model.db.ImagesDAO

@Database(entities = [ImageEntity::class], version = 3)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun imageDao(): ImagesDAO
}
