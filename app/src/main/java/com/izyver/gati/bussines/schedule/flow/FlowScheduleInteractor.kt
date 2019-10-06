package com.izyver.gati.bussines.schedule.flow

import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.models.ScheduleType.DAYTIME
import com.izyver.gati.bussines.models.ScheduleType.DISTANCE
import com.izyver.gati.data.database.flow.IScheduleTypeSource

class FlowScheduleInteractor(private val source: IScheduleTypeSource) : IFlowScheduleInteractor{

    override suspend fun getNecessaryScheduleTypeForDisplay(): ScheduleType {
        return source.getScheduleType()
    }

    override suspend fun changeDisplayedScheduleType(): ScheduleType {
        val oldType = source.getScheduleType()
        val newType = if (oldType == DAYTIME) DISTANCE else DAYTIME
        source.saveScheduleType(newType)
        return newType
    }
}