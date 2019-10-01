package com.izyver.gati.data.schedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.data.database.ILocalScheduleDataSource
import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.data.network.ScheduleNetworkDto
import com.izyver.gati.exception.DateParseException
import com.izyver.gati.utils.parseStandardGatiDate
import com.izyver.gati.utils.textAsBitmap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import java.util.*
import kotlin.collections.HashMap

private const val NETWORK_CHANEL_KEY = "network_chanel"
private const val DATABASE_CHANEL_KEY = "network_chanel"

class ScheduleRepository(
        private val remoteSource: IRemoteScheduleDataSource,
        private val localSource: ILocalScheduleDataSource) : IScheduleRepository {

    private var softImageCache = SoftReference<MutableMap<String, ScheduleImageDto>>(hashMapOf())

    private val channelCache = HashMap<String, Channel<ScheduleImageDto>>(2)

    override fun loadNewImageFromNetwork(): Channel<ScheduleImageDto> {
        if (channelCache[NETWORK_CHANEL_KEY] != null) return channelCache[NETWORK_CHANEL_KEY] as Channel<ScheduleImageDto>
        val channel = Channel<ScheduleImageDto>()
        channelCache[NETWORK_CHANEL_KEY] = channel

        GlobalScope.launch {
            val existingSchedules: List<ScheduleNetworkDto> = remoteSource.getExistingSchedule()
            val localScheduleDescriptions: List<ScheduleDbDtoWithoutBitmap> = localSource.getScheduleDescription()
            existingSchedules.forEach { remoteSchedule ->

                val dateFromRemote = parseStandardGatiDate(remoteSchedule.date)
                        ?: throw DateParseException(remoteSchedule.date)
                val day = Days.from(dateFromRemote)

                if (localScheduleDescriptions.size >= day.index) return@forEach
                val dateFromLocal = parseStandardGatiDate(localScheduleDescriptions[day.index].date)
                        ?: throw DateParseException(localScheduleDescriptions[day.index].date)

                if (!isActualScheduleDate(dateFromLocal, dateFromRemote)) return@forEach

                val actualSchedule: ScheduleImageDto
                val image: Bitmap = remoteSource.getBitmapBy(remoteSchedule)
                actualSchedule = ScheduleImageDto(image, day, dateFromRemote, remoteSchedule.image ?: "", true)
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
        if (channelCache[DATABASE_CHANEL_KEY] != null)
            return channelCache[DATABASE_CHANEL_KEY] as Channel<ScheduleImageDto>
        val channel = Channel<ScheduleImageDto>()
        GlobalScope.launch {
            val localSchedules: List<ScheduleDbDto> = localSource.getSchedule()
            val currentDate = Date()
            localSchedules.forEach {localSchedule ->
                val imageBitmap = bitmapFromBytes(localSchedule.image)
                val date = (parseStandardGatiDate(localSchedule.date)
                        ?: throw DateParseException(localSchedule.date))
                val day = Days.from(date)
                val scheduleImageDto = ScheduleImageDto(
                        imageBitmap,
                        day,
                        date,
                        localSchedule.title ?: "",
                        isActualScheduleDate(date, currentDate)
                )
                channel.send(scheduleImageDto)
            }
        }
        return channel
    }

    private fun cacheImage(day: Days, actualSchedule: ScheduleImageDto) {
        val cacheImageMap = softImageCache.get()
        if (cacheImageMap != null) {
            cacheImageMap[day.name] = actualSchedule
        } else {
            softImageCache = SoftReference(hashMapOf(Pair(day.name, actualSchedule)))
        }
    }

    private fun isActualScheduleDate(oldDate: Date, newDate: Date): Boolean {
        return newDate.time < oldDate.time
    }

    private fun bitmapFromBytes(bytes: ByteArray?): Bitmap?{
        if (bytes == null) return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}

