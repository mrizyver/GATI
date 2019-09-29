package com.izyver.gati.di

import com.izyver.gati.BuildConfig.DEBUG
import com.izyver.gati.data.network.ScheduleType.DAYTIME
import com.izyver.gati.data.network.ScheduleType.DISTANCE
import com.izyver.gati.bussines.schedule.ScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.FlowScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.IFlowScheduleInteractor
import com.izyver.gati.data.DaytimeRepository
import com.izyver.gati.data.DistanceRepository
import com.izyver.gati.data.TestRepository
import com.izyver.gati.presentation.schedule.ScheduleViewModel
import com.izyver.gati.presentation.schedule.flow.FlowScheduleViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

val appModule = module {

    viewModel(qualifier = StringQualifier(DAYTIME.name)) {
        ScheduleViewModel(ScheduleInteractor(if (DEBUG) TestRepository() else DaytimeRepository() ))
    }

    viewModel(qualifier = StringQualifier(DISTANCE.name)) {
        ScheduleViewModel(ScheduleInteractor(if (DEBUG) TestRepository() else DistanceRepository()))
    }

    viewModel { FlowScheduleViewModel(get()) }

    factory<IFlowScheduleInteractor> { FlowScheduleInteractor() }
}