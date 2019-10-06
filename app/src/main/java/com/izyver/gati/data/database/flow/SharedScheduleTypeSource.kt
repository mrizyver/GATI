package com.izyver.gati.data.database.flow

import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.data.android.GatiPref


class SharedScheduleTypeSource(private val pref: GatiPref) : IScheduleTypeSource {

    private val defaultScheduleType = ScheduleType.DAYTIME

    override fun saveScheduleType(type: ScheduleType) {
        pref.saveScheduleType(type.name)
    }

    override fun getScheduleType(): ScheduleType {
        val name = pref.getScheduleTypeName() ?: return defaultScheduleType
        return ScheduleType.valueOf(name)
    }
}