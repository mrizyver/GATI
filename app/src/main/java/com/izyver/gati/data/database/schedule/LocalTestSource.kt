package com.izyver.gati.data.database.schedule

import android.graphics.Bitmap
import android.graphics.Color
import com.izyver.gati.bussines.SCHEDULE_TYPE_DAYTIME
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto
import com.izyver.gati.data.database.schedule.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.network.models.ScheduleNetworkDto
import com.izyver.gati.utils.formatStandardGatiDate
import com.izyver.gati.utils.parseStandardGatiDate
import com.izyver.gati.utils.textAsBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.lang.ref.SoftReference
import java.util.*
import kotlin.collections.ArrayList

class LocalTestSource : ILocalScheduleDataSource {

    override fun getScheduleByDay(day: Days): ScheduleDbDto? {
        for (scheduleDbDto in list) {
            if (Days.from(parseStandardGatiDate(scheduleDbDto.date) ?: continue) == day){
                return scheduleDbDto
            }
        }
        return null
    }

    private val list: List<ScheduleDbDto> = getSchedules()
    private var softCacheImages = SoftReference<MutableList<ScheduleDbDto>>(null)

    override fun getCachedSchedules(): List<ScheduleDbDto> {
        return softCacheImages.get() ?: listOf()
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
        softCacheImages = SoftReference(list.toMutableList())
        return list
    }

    override fun getScheduleByDate(date: String?): ScheduleDbDto? {
        for (scheduleDbDto in list) {
            if (scheduleDbDto.date == date){
                addScheduleToCache(scheduleDbDto)
                return scheduleDbDto
            }
        }
        return null
    }

    override fun saveSchedule(schedyle: ScheduleNetworkDto, image: ByteArray?) {

    }

    private fun getSchedules(): List<ScheduleDbDto> {
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
                    SCHEDULE_TYPE_DAYTIME,
                    getImageBytes(day),
                    day.name,
                    day.name
            )
            list.add(scheduleDbDto)
        }
        return list
    }

    private fun addScheduleToCache(scheduleDbDto: ScheduleDbDto) {
        scheduleDbDto.image = toBytes(textAsBitmap("${scheduleDbDto.dayWeek} from cache", 100F, Color.RED))
        var cache: MutableList<ScheduleDbDto>? = softCacheImages.get()
        if (cache == null) {
            cache = mutableListOf(scheduleDbDto)
            softCacheImages = SoftReference(cache)
        } else {
            val index = cache.indexOf(scheduleDbDto)
            if (index == -1) {
                cache.add(scheduleDbDto)
            } else {
                cache[index] = scheduleDbDto
            }
        }
    }

    private fun getImageBytes(day: Days): ByteArray {
        val bitmap = textAsBitmap("${day.name} from database", 130F, Color.GRAY)
        return toBytes(bitmap)
    }

    private fun toBytes(bitmap: Bitmap): ByteArray {
        val buffer = ByteArrayOutputStream(bitmap.width * bitmap.height)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer)
        return buffer.toByteArray()
    }
}