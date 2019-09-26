package com.izyver.gati.presentation.schedule

import androidx.fragment.app.Fragment
import com.izyver.gati.presentation.schedule.ScheduleViewModel.Type.DAYTIME
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.StringQualifier

abstract class ScheduleFragment : Fragment() {

    protected abstract val viewModel: ScheduleViewModel



    class Daytime : ScheduleFragment(){
        override val viewModel: ScheduleViewModel by viewModel(StringQualifier(DAYTIME))
    }

    class Distance : ScheduleFragment(){
        override val viewModel: ScheduleViewModel by viewModel(StringQualifier(DAYTIME))
    }
}