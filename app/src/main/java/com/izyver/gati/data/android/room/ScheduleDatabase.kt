package com.izyver.gati.data.android.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto


@Database(entities = [ScheduleDbDto::class], version = 3)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}
