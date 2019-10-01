package com.izyver.gati.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ScheduleDbDto(
        @PrimaryKey
        @ColumnInfo(name = "key")
        var key: String?,
        @ColumnInfo(name = "image_id")
        var id: Int?,
        @ColumnInfo(name = "date")
        var date: String?,
        @ColumnInfo(name = "type")
        var type: Int?,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        var image: ByteArray?,
        @ColumnInfo(name = "title")
        var title: String?,
        @ColumnInfo(name = "day_week")
        var dayWeek: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleDbDto

        if (key != other.key) return false
        if (id != other.id) return false
        if (date != other.date) return false
        if (type != other.type) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image!!.contentEquals(other.image!!)) return false
        } else if (other.image != null) return false
        if (title != other.title) return false
        if (dayWeek != other.dayWeek) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.hashCode() ?: 0
        result = 31 * result + (id ?: 0)
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + (type ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (dayWeek?.hashCode() ?: 0)
        return result
    }
}


