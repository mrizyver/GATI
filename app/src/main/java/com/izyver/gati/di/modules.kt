package com.izyver.gati.di

import com.izyver.gati.bussines.schedule.ScheduleInteractor
import com.izyver.gati.data.DaytimeRepository
import com.izyver.gati.data.DistanceRepository
import com.izyver.gati.presentation.schedule.ScheduleViewModel
import com.izyver.gati.presentation.schedule.ScheduleViewModel.Type.DAYTIME
import com.izyver.gati.presentation.schedule.ScheduleViewModel.Type.DISTANCE
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

val appModule = module {

    viewModel(qualifier = StringQualifier(DAYTIME)) {
        ScheduleViewModel(ScheduleInteractor(DaytimeRepository()))
    }

    viewModel(qualifier = StringQualifier(DISTANCE)) {
        ScheduleViewModel(ScheduleInteractor(DistanceRepository()))
    }
}