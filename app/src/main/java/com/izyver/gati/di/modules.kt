package com.izyver.gati.di

import com.izyver.gati.BuildConfig.DEBUG
import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.schedule.ScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.FlowScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.IFlowScheduleInteractor
import com.izyver.gati.data.schedule.DaytimeRepository
import com.izyver.gati.data.schedule.DistanceRepository
import com.izyver.gati.data.schedule.TestRepository
import com.izyver.gati.presentation.schedule.ScheduleViewModel
import com.izyver.gati.presentation.schedule.flow.FlowScheduleViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

val appModule = module {

    viewModel(qualifier = StringQualifier(ScheduleType.DAYTIME.name)) {
        ScheduleViewModel(ScheduleInteractor(if (DEBUG) TestRepository() else DaytimeRepository() ))
    }

    viewModel(qualifier = StringQualifier(ScheduleType.DISTANCE.name)) {
        ScheduleViewModel(ScheduleInteractor(if (DEBUG) TestRepository() else DistanceRepository()))
    }

    viewModel { FlowScheduleViewModel(get()) }

    factory<IFlowScheduleInteractor> { FlowScheduleInteractor() }
}