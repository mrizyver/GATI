package com.izyver.gati.bussines.schedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.bussines.schedule.usecases.IScheduleDateUseCase
import com.izyver.gati.data.database.ILocalScheduleDataSource
import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.database.models.ScheduleDbModel
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.data.network.ScheduleNetworkDto
import com.izyver.gati.exception.DateParseException
import com.izyver.gati.utils.parseStandardGatiDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

private const val NETWORK_CHANEL_KEY = "network_chanel"
private const val DATABASE_CHANEL_KEY = "network_chanel"

class ScheduleInteractor(private val remoteSource: IRemoteScheduleDataSource,
                         private val localSource: ILocalScheduleDataSource,
                         private val dateUseCase: IScheduleDateUseCase) : IScheduleInteractor {

    private val channelCache = HashMap<String, Channel<ScheduleImageDto>>(2)

    override fun loadNetworkImages(): Channel<ScheduleImageDto> {
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

                if (localScheduleDescriptions.containsByDate(remoteSchedule.date)) return@forEach

                if (localScheduleDescriptions.size > day.index) {
                    val dateFromLocal = parseStandardGatiDate(localScheduleDescriptions[day.index].date)
                            ?: throw DateParseException(localScheduleDescriptions[day.index].date)

                    if (!isNewScheduleDate(dateFromLocal, dateFromRemote)) return@forEach
                }

                val actualSchedule: ScheduleImageDto
                val image: Bitmap? = remoteSource.getBitmapBy(remoteSchedule)
                actualSchedule = ScheduleImageDto(image, day, dateFromRemote, remoteSchedule.image
                        ?: "", true)
                channel.send(actualSchedule)
            }
            channel.close()
            channelCache.remove(NETWORK_CHANEL_KEY)
        }
        return channel
    }

    override fun loadCacheImage(): Channel<ScheduleImageDto> {
        if (channelCache[DATABASE_CHANEL_KEY] != null)
            return channelCache[DATABASE_CHANEL_KEY] as Channel<ScheduleImageDto>
        val channel = Channel<ScheduleImageDto>()
        channelCache[DATABASE_CHANEL_KEY] = channel

        GlobalScope.launch {
            var cachedSchedulesDb: MutableList<ScheduleDbDto> = localSource.getCachedSchedules().toMutableList()
            val descriptionSchedules: List<ScheduleDbDtoWithoutBitmap> = localSource.getScheduleDescription()
            if (cachedSchedulesDb.isEmpty()) {
                cachedSchedulesDb = localSource.getStoredSchedule().toMutableList()
            } else if (cachedSchedulesDb.size < descriptionSchedules.size) {
                descriptionSchedules.forEach { descSchedule ->
                    if (!cachedSchedulesDb.containsByDate(descSchedule.date)) {
                        val scheduleDb = localSource.getScheduleByDate(descSchedule.date)
                                ?: return@forEach
                        cachedSchedulesDb.add(scheduleDb)
                    }
                }
            }
            postSchedules(channel, cachedSchedulesDb)
            channel.close()
            channelCache.remove(DATABASE_CHANEL_KEY)
        }
        return channel
    }

    private fun List<ScheduleDbModel>.containsByDate(strDate: String?): Boolean {
        this.forEach { if (it.date == strDate) return true }
        return false
    }

    private suspend fun postSchedules(channel: Channel<ScheduleImageDto>, schedules: List<ScheduleDbDto>) {
        schedules.forEach { scheduleDb ->
            postSchedule(scheduleDb, channel)
        }
    }

    private suspend fun postSchedule(scheduleDb: ScheduleDbDto, channel: Channel<ScheduleImageDto>) {
        val imageBitmap = bitmapFromBytes(scheduleDb.image)
        val date = (parseStandardGatiDate(scheduleDb.date)
                ?: throw DateParseException(scheduleDb.date))
        val day = Days.from(date)
        val scheduleImageDto = ScheduleImageDto(
                imageBitmap,
                day,
                date,
                scheduleDb.title ?: "",
                dateUseCase.isScheduleDateActual(date)
        )
        channel.send(scheduleImageDto)
    }

    private fun isNewScheduleDate(oldDate: Date, newDate: Date): Boolean {
        return newDate.time > oldDate.time
    }

    private fun bitmapFromBytes(bytes: ByteArray?): Bitmap? {
        if (bytes == null) return null
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}