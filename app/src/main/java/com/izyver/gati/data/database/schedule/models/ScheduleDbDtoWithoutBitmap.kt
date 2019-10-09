package com.izyver.gati.data.database.schedule.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ScheduleDbDtoWithoutBitmap(
        @PrimaryKey
        @ColumnInfo(name = "key")
        override var key: String,
        @ColumnInfo(name = "image_id")
        override var id: Int?,
        @ColumnInfo(name = "date")
        override var date: String?,
        @ColumnInfo(name = "type")
        override var type: Int?,
        @ColumnInfo(name = "title")
        override var title: String?,
        @ColumnInfo(name = "day_week")
        override var dayWeek: String?
) : ScheduleDbModel
