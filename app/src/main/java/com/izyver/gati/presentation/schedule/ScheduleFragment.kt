package com.izyver.gati.presentation.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.izyver.gati.R
import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.bussines.models.ScheduleType.DAYTIME
import com.izyver.gati.bussines.models.ScheduleType.DISTANCE
import com.izyver.gati.presentation.BaseFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.StringQualifier

abstract class ScheduleFragment : BaseFragment() {

    protected abstract val viewModel: ScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeScheduleAdapter()
    }

    private fun initializeScheduleAdapter() {
        val cardAdapter = ScheduleCardAdapter()
        scheduleRecyclerView.adapter = cardAdapter
        scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        viewModel.loadImages().observe(this, Observer { cardAdapter.setValues(it) })
    }

    companion object {
        fun getTag(scheduleType: ScheduleType): String {
            return when (scheduleType) {
                DAYTIME -> Daytime.TAG
                DISTANCE -> Distance.TAG
            }
        }

        fun newInstance(scheduleType: ScheduleType): ScheduleFragment {
            return when (scheduleType) {
                DISTANCE -> Distance()
                DAYTIME -> Daytime()
            }
        }
    }

    class Daytime : ScheduleFragment() {
        override val viewModel: ScheduleViewModel by viewModel(StringQualifier(DAYTIME.name))

        companion object {
            const val TAG: String = "ScheduleFragmentDaytime"
        }
    }

    class Distance : ScheduleFragment() {
        override val viewModel: ScheduleViewModel by viewModel(StringQualifier(DISTANCE.name))

        companion object {
            const val TAG: String = "ScheduleFragmentDistance"
        }
    }
}