package com.izyver.gati.data.schedule

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import com.izyver.gati.R
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.data.database.ILocalScheduleDataSource
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.utils.parseDateFromApi
import com.izyver.gati.utils.textAsBitmap
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.ref.SoftReference
import java.util.*
import kotlin.collections.HashMap

private const val NETWORK_CHANEL_KEY = "network_chanel"

class ScheduleRepository(
        private val remoteSource: IRemoteScheduleDataSource,
        private val localSource: ILocalScheduleDataSource) : IScheduleRepository {

    private var softImageCache = SoftReference<MutableMap<String, ScheduleImageDto>>(hashMapOf())

    private val channelCache = HashMap<String, Any>(2)

    override fun loadNewImageFromNetwork(): Channel<ScheduleImageDto> {
        if (channelCache[NETWORK_CHANEL_KEY] != null) return channelCache[NETWORK_CHANEL_KEY] as Channel<ScheduleImageDto>
        val channel = Channel<ScheduleImageDto>()
        channelCache[NETWORK_CHANEL_KEY] = channel

        GlobalScope.launch {
            val existingSchedules = remoteSource.getExistingSchedule()
            existingSchedules.forEach { schedule ->
                val image: Bitmap = remoteSource.getBitmapBy(schedule)
                val date = parseDateFromApi(schedule.date)
                        ?: throw RuntimeException("We can't create ${ScheduleImageDto::class.java.simpleName} with null date")
                val day = Days.from(date)
                val actualSchedule = ScheduleImageDto(image, day, date, schedule.image ?: "", false)
                cacheImage(day, actualSchedule.copy(image = textAsBitmap("${day.name} from cache", 150F, Color.GRAY)))
                channel.send(actualSchedule)
            }
            channel.close()
            channelCache.remove(NETWORK_CHANEL_KEY)
        }
        return channel
    }

    override fun isCacheEmpty(): Boolean {
        val map = softImageCache.get() ?: return true
        return map.isEmpty()
    }

    override fun getImageFromCache(): List<ScheduleImageDto> {
        val map = softImageCache.get() ?: hashMapOf()
        return map.values as List<ScheduleImageDto>
    }

    override suspend fun isExistImagesInDB(): Boolean {
        val scheduleDescriptions: List<ScheduleDbDtoWithoutBitmap> = localSource.getScheduleDescription()
        return scheduleDescriptions.isNotEmpty()
    }

    override suspend fun loadImageFromStorage(): Channel<ScheduleImageDto> {
        return Channel()
    }

    private fun cacheImage(day: Days, actualSchedule: ScheduleImageDto) {
        val cacheImageMap = softImageCache.get()
        if (cacheImageMap != null) {
            cacheImageMap[day.name] = actualSchedule
        } else {
            softImageCache = SoftReference(hashMapOf(Pair(day.name, actualSchedule)))
        }
    }
}

