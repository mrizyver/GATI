package com.izyver.gati.presentation.schedule.flow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.schedule.flow.IFlowScheduleInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FlowScheduleViewModel(private val flowInteractor: IFlowScheduleInteractor) : ViewModel() {

    init {
        println()
    }

    private val _scheduleType: MutableLiveData<ScheduleType> = MutableLiveData()
    val displayedSchedule: LiveData<ScheduleType> = _scheduleType

    fun loadNecasseryScheduleType() {
        GlobalScope.launch {
            val necessaryScheduleType = flowInteractor.getNecessaryScheduleTypeForDisplay()
            _scheduleType.postValue(necessaryScheduleType)
        }
    }

    fun onSwitchScheduleButtonClicked() {
        GlobalScope.launch {
            val newScheduleType = flowInteractor.changeDisplayedScheduleType()
            _scheduleType.postValue(newScheduleType)
        }
    }
}