package com.izyver.gati.presentation.schedule

import android.view.View

interface OnScheduleClickListener {
    fun onLongClick(view: View, index: Int, x: Float, y: Float)
    fun onShortClick(view: View, index: Int)
}