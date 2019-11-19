package com.izyver.gati.data.database.schedule

import com.izyver.gati.bussines.SCHEDULE_TYPE_DAYTIME
import com.izyver.gati.bussines.SCHEDULE_TYPE_DISTANCE
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.android.room.ScheduleDao
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto
import com.izyver.gati.data.database.schedule.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.database.schedule.models.ScheduleDbModel
import com.izyver.gati.data.network.models.ScheduleNetworkDto
import com.izyver.gati.extentions.findByDate
import com.izyver.gati.extentions.findByDay
import com.izyver.gati.extentions.put
import java.lang.ref.SoftReference

abstract class LocalScheduleSource(
        private val scheduleDao: ScheduleDao,
        private val scheduleType: Int
) : ILocalScheduleDataSource {

    private var softScheduleList: SoftReference<MutableList<ScheduleDbDto>> = SoftReference(mutableListOf())

    override fun getScheduleByDay(day: Days): ScheduleDbDto? {
        val list: MutableList<ScheduleDbDto>? = softScheduleList.get()
        var scheduleDbDto: ScheduleDbDto? = list?.findByDay(day) as ScheduleDbDto?

        if (scheduleDbDto == null) {
            val scheduleDescriptions = getScheduleDescription().toMutableList()
            val foundSchedule: ScheduleDbModel? = scheduleDescriptions.findByDay(day)
            scheduleDbDto = scheduleDao.getEntityByKey(foundSchedule?.key ?: return null)
            cache(scheduleDbDto ?: return null)
        }
        return scheduleDbDto
    }

    override fun saveSchedule(schedule: ScheduleNetworkDto, image: ByteArray?) {

        val scheduleDb = ScheduleDbDto(
                "${schedule.dayWeek}${schedule.type}",
                schedule.id,
                schedule.date,
                schedule.type,
                image,
                schedule.title,
                schedule.dayWeek
        )

        scheduleDao.putImageEntity(scheduleDb)
    }

    override fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap> {
        return scheduleDao.getDescriptionEntities(scheduleType)
    }

    override fun getStoredSchedule(): List<ScheduleDbDto> {
        val schedules = scheduleDao.getEntitiesByType(scheduleType)
        softScheduleList = SoftReference(schedules.toMutableList())
        return schedules
    }

    override fun getCachedSchedules(): List<ScheduleDbDto> {
        return softScheduleList.get() ?: listOf()
    }

    override fun getScheduleByDate(date: String?): ScheduleDbDto? {
        if (date == null) return null
        val list = softScheduleList.get()
        val scheduleDbDto: ScheduleDbDto? = list?.findByDate(date) as ScheduleDbDto?
                ?: scheduleDao.getEntityByDate(date)
        cache(scheduleDbDto ?: return null)
        return scheduleDbDto
    }

    @Suppress("UNCHECKED_CAST")
    private fun cache(scheduleDbDto: ScheduleDbDto) {
        val list: MutableList<ScheduleDbModel>? = softScheduleList.get() as MutableList<ScheduleDbModel>?
        if (list == null) {
            softScheduleList = SoftReference(mutableListOf(scheduleDbDto))
        } else {
            list.put(scheduleDbDto)
        }
    }

    class DaytimeSource(scheduleDao: ScheduleDao) : LocalScheduleSource(scheduleDao, SCHEDULE_TYPE_DAYTIME)
    class DistanceSource(scheduleDao: ScheduleDao) : LocalScheduleSource(scheduleDao, SCHEDULE_TYPE_DISTANCE)
}