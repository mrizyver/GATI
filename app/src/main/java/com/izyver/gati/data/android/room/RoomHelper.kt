package com.izyver.gati.data.android.room

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomHelper {

    fun buildDatabase(context: Context): ScheduleDatabase {
        return Room.databaseBuilder(context, ScheduleDatabase::class.java, "gati_database")
                .addMigrations(migrationFrom2To3())
                .build()
    }

    private fun migrationFrom2To3(): Migration {
        return object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
    }
}