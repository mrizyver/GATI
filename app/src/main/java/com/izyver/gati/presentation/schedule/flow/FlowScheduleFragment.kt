package com.izyver.gati.presentation.schedule.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.izyver.gati.R
import com.izyver.gati.bussines.models.ScheduleType
import com.izyver.gati.presentation.BaseFragment
import com.izyver.gati.presentation.schedule.ScheduleFragment
import kotlinx.android.synthetic.main.fragment_shcedule_flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FlowScheduleFragment : BaseFragment() {

    private val flowViewModel: FlowScheduleViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_shcedule_flow, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flowViewModel.displayedSchedule.observe(this, Observer<ScheduleType> { displaySchedule(it) })
        flowViewModel.loadNecasseryScheduleType()

        scheduleFlowSwitchScheduleButton.setOnClickListener { flowViewModel.onSwitchScheduleButtonClicked() }
    }

    private fun displaySchedule(scheduleType: ScheduleType) {
        val tag = ScheduleFragment.getTag(scheduleType)
        val fragmentManager = fragmentManager ?: return
        val fragment = fragmentManager.findFragmentByTag(tag)
                ?: ScheduleFragment.newInstance(scheduleType)
        fragmentManager
                .beginTransaction()
                .replace(R.id.flowFragmentContainer, fragment, tag)
                .commit()
    }

    companion object{
        const val TAG: String = "FlowScheduleFragment"
    }
}