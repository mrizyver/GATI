package com.izyver.gati.di

import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.schedule.ScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.FlowScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.IFlowScheduleInteractor
import com.izyver.gati.bussines.schedule.usecases.DateUseCaseWeekBased
import com.izyver.gati.data.database.ILocalScheduleDataSource
import com.izyver.gati.data.database.LocalDaytimeSource
import com.izyver.gati.data.database.LocalDistanceSource
import com.izyver.gati.data.database.LocalTestSource
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.data.network.RemoteDaytimeSource
import com.izyver.gati.data.network.RemoteDistanceSource
import com.izyver.gati.data.network.RemoteTestSource
import com.izyver.gati.presentation.schedule.ScheduleViewModel
import com.izyver.gati.presentation.schedule.flow.FlowScheduleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module

val scheduleModule = module {

    viewModel(StringQualifier(ScheduleType.DAYTIME.name)) {
        ScheduleViewModel(ScheduleInteractor(get(TypeQualifier(RemoteTestSource::class)), get(TypeQualifier(LocalTestSource::class)), DateUseCaseWeekBased()))
    }

    viewModel(StringQualifier(ScheduleType.DISTANCE.name)) {
        ScheduleViewModel(ScheduleInteractor(get(TypeQualifier(RemoteTestSource::class)), get(TypeQualifier(LocalTestSource::class)), DateUseCaseWeekBased()))
    }

    viewModel { FlowScheduleViewModel(get()) }

    factory<IFlowScheduleInteractor> { FlowScheduleInteractor() }

    single<ILocalScheduleDataSource>(TypeQualifier(LocalDaytimeSource::class)) { LocalDaytimeSource() }
    single<ILocalScheduleDataSource>(TypeQualifier(LocalDistanceSource::class)) { LocalDistanceSource() }
    single<ILocalScheduleDataSource>(TypeQualifier(LocalTestSource::class)) { LocalTestSource() }

    single<IRemoteScheduleDataSource>(TypeQualifier(RemoteDaytimeSource::class)) { RemoteDaytimeSource() }
    single<IRemoteScheduleDataSource>(TypeQualifier(RemoteDistanceSource::class)) { RemoteDistanceSource() }
    single<IRemoteScheduleDataSource>(TypeQualifier(RemoteTestSource::class)) { RemoteTestSource() }
}