package com.izyver.gati.data.database

import android.graphics.Bitmap
import android.graphics.Color
import com.izyver.gati.bussines.SCHEDULE_TUPE_API_DAYTIME
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.utils.formatStandardGatiDate
import com.izyver.gati.utils.textAsBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class LocalTestSource : ILocalScheduleDataSource {

    private val list: List<ScheduleDbDto> = getSchesdules()

    override fun getCachedSchedules(): List<ScheduleDbDto> {
        return listOf()
    }

    override fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap> {
        val newList = ArrayList<ScheduleDbDtoWithoutBitmap>(6)
        runBlocking {
            delay(300)
            list.forEach {
                newList.add(ScheduleDbDtoWithoutBitmap(
                        it.key, it.id, it.date, it.type, it.title, it.dayWeek))
            }
        }
        return newList

    }

    override fun getStoredSchedule(): List<ScheduleDbDto> {
        runBlocking {
            delay(1500)
        }
        return list
    }

    private fun getSchesdules(): List<ScheduleDbDto> {
        val list = ArrayList<ScheduleDbDto>(6)
        repeat(6) { index ->

            val calendar = Calendar.getInstance()
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            calendar.set(Calendar.DAY_OF_YEAR, dayOfYear - index + 3)
            val day = Days.from(calendar)
            val scheduleDbDto = ScheduleDbDto(
                    "key$index",
                    index,
                    formatStandardGatiDate(calendar.time),
                    SCHEDULE_TUPE_API_DAYTIME,
                    getImageBytes(day),
                    day.name,
                    day.name
            )
            list.add(scheduleDbDto)
        }
        return list
    }

    private fun getImageBytes(day: Days): ByteArray {
        val bitmap = textAsBitmap("${day.name} from database", 130F, Color.GRAY)
        val buffer = ByteArrayOutputStream(bitmap.width * bitmap.height)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer)
        return buffer.toByteArray()
    }
}