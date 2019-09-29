package com.izyver.gati.bussines.schedule.flow

import com.izyver.gati.data.network.ScheduleType

class FlowScheduleInteractor : IFlowScheduleInteractor{

    override suspend fun getNecessaryScheduleTypeForDisplay(): ScheduleType {
        return ScheduleType.DISTANCE
    }

    override suspend fun changeDisplayedScheduleType(): ScheduleType {
        return ScheduleType.DISTANCE
    }
}