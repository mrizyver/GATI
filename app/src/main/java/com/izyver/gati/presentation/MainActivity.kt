package com.izyver.gati.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.izyver.gati.R
import com.izyver.gati.presentation.schedule.flow.FlowScheduleFragment

open class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(FlowScheduleFragment(), FlowScheduleFragment.TAG)
    }

    private fun replaceFragment(fragment: BaseFragment, tag: String? = null) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.activityMainFragmentContainer, fragment, tag)
                .commit()
    }
}
