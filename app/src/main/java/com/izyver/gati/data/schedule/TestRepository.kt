package com.izyver.gati.data.schedule

import android.graphics.Bitmap
import com.izyver.gati.R
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.SoftReference
import java.util.*

class TestRepository : ScheduleRepository {

    private val softMap = SoftReference<Map<String, ScheduleImageDto>>(hashMapOf())

    override fun loadNewImageFromNetwork(): Channel<ScheduleImageDto> {
        val channel = Channel<ScheduleImageDto>()
        GlobalScope.launch {
            repeat(7) {
                delay(2000)
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, it)
                channel.send(generateSchedule(calendar, it))
            }
            channel.close()
        }
        return channel
    }

    override fun isCacheEmpty(): Boolean {
        val map = softMap.get() ?: return true
        return map.isEmpty()
    }

    override fun getImageFromCache(): List<ScheduleImageDto> {
        val map = softMap.get() ?: hashMapOf()
        return map.values as List<ScheduleImageDto>
    }

    override fun isExistImagesInDB(): Boolean {
        return false
    }

    override fun loadImageFromStorage(): Channel<ScheduleImageDto> {
        return Channel()
    }

    private fun generateSchedule(calendar: Calendar, id: Int): ScheduleImageDto {
        val day = Days.values()[id]
        return ScheduleImageDto(createBitmap(day), day, calendar.time, "schedule$id", id % 2 == 0)
    }

    private fun createBitmap(day: Days): Bitmap? {
        return when (day) {
            Days.MONDAY -> getBitmap(R.drawable.mon)
            Days.TUESDAY -> getBitmap(R.drawable.tue)
            Days.WEDNESDAY -> getBitmap(R.drawable.wed)
            Days.THURSDAY -> getBitmap(R.drawable.thu)
            Days.FRIDAY -> getBitmap(R.drawable.fri)
            Days.SATURDAY -> getBitmap(R.drawable.sat)
            Days.SUNDAY -> getBitmap(R.drawable.sun)
        }
    }

    private fun getBitmap(id: Int): Bitmap? {
        return try {
            Picasso.get().load(id).get()
        } catch (e: IOException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }
}