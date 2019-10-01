package com.izyver.gati.data.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.izyver.gati.data.database.models.ScheduleDbDto

@Dao
interface ScheduleDAO {

    @Query("SELECT * FROM images")
    fun getAll(): List<ScheduleDbDto>

    @Query("SELECT * FROM images WHERE `date` = :date")
    fun getEntityByDate(date: String): ScheduleDbDto

    @Query("SELECT * FROM images WHERE `key` = :imageKey")
    fun getEntityByKey(imageKey: String): ScheduleDbDto

    @Query("SELECT * FROM images WHERE `type` = :type")
    fun getEntitiesByType(type: Int): List<ScheduleDbDto>

    @Query("SELECT * FROM images WHERE `key` IN (:imageKeys)")
    fun getEntityByKeys(imageKeys: Array<String>): List<ScheduleDbDto>

    @Query("SELECT title FROM images WHERE `type` = :type")
    fun getListOfImageNames(type: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putImageEntity(scheduleDbDto: ScheduleDbDto)
}
