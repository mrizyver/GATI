package com.izyver.gati.bussines.schedule.flow

import com.izyver.gati.bussines.models.ScheduleType

interface IFlowScheduleInteractor {

    suspend fun getNecessaryScheduleTypeForDisplay(): ScheduleType

    /**
     * @return new schedule type for display
     */
    suspend fun changeDisplayedScheduleType(): ScheduleType

}