package com.izyver.gati.data.database.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ScheduleDbDtoWithoutBitmap(
        @PrimaryKey
        @ColumnInfo(name = "key")
        var key: String?,
        @ColumnInfo(name = "image_id")
        var id: Int?,
        @ColumnInfo(name = "date")
        var date: String?,
        @ColumnInfo(name = "type")
        var type: Int?,
        @ColumnInfo(name = "title")
        var title: String?,
        @ColumnInfo(name = "day_week")
        var dayWeek: String?
)
