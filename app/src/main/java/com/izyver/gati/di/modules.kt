package com.izyver.gati.di

import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.schedule.ScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.FlowScheduleInteractor
import com.izyver.gati.bussines.schedule.flow.IFlowScheduleInteractor
import com.izyver.gati.bussines.schedule.usecases.DateUseCaseWeekBased
import com.izyver.gati.data.android.GatiApi
import com.izyver.gati.data.android.GatiPref
import com.izyver.gati.data.database.flow.IScheduleTypeSource
import com.izyver.gati.data.database.flow.SharedScheduleTypeSource
import com.izyver.gati.data.database.schedule.ILocalScheduleDataSource
import com.izyver.gati.data.database.schedule.LocalDaytimeSource
import com.izyver.gati.data.database.schedule.LocalDistanceSource
import com.izyver.gati.data.database.schedule.LocalTestSource
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.data.network.RemoteDaytimeSource
import com.izyver.gati.data.network.RemoteDistanceSource
import com.izyver.gati.data.network.RemoteTestSource
import com.izyver.gati.presentation.schedule.ScheduleViewModel
import com.izyver.gati.presentation.schedule.flow.FlowScheduleViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val scheduleModule = module {

    viewModel(StringQualifier(ScheduleType.DAYTIME.name)) {
        ScheduleViewModel(ScheduleInteractor(get(TypeQualifier(RemoteTestSource::class)), get(TypeQualifier(LocalTestSource::class)), DateUseCaseWeekBased()))
    }

    viewModel(StringQualifier(ScheduleType.DISTANCE.name)) {
        ScheduleViewModel(ScheduleInteractor(get(TypeQualifier(RemoteTestSource::class)), get(TypeQualifier(LocalTestSource::class)), DateUseCaseWeekBased()))
    }

    single<ILocalScheduleDataSource>(TypeQualifier(LocalDaytimeSource::class)) { LocalDaytimeSource() }
    single<ILocalScheduleDataSource>(TypeQualifier(LocalDistanceSource::class)) { LocalDistanceSource() }
    single<ILocalScheduleDataSource>(TypeQualifier(LocalTestSource::class)) { LocalTestSource() }

    single<IRemoteScheduleDataSource>(TypeQualifier(RemoteDaytimeSource::class)) { RemoteDaytimeSource(get()) }
    single<IRemoteScheduleDataSource>(TypeQualifier(RemoteDistanceSource::class)) { RemoteDistanceSource(get()) }
    single<IRemoteScheduleDataSource>(TypeQualifier(RemoteTestSource::class)) { RemoteTestSource() }



    viewModel { FlowScheduleViewModel(get()) }

    factory<IFlowScheduleInteractor> { FlowScheduleInteractor(get()) }
    factory<IScheduleTypeSource> {SharedScheduleTypeSource(get())}

    single { GatiPref(androidApplication()) }
    single { createGatiApi() }
}

private fun createGatiApi(): GatiApi{
    val retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    return retrofit.create(GatiApi::class.java)
}