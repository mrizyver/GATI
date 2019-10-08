package com.izyver.gati.presentation.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.izyver.gati.R
import com.izyver.gati.bussines.models.ScheduleState.OLD
import com.izyver.gati.old.utils.Util.getScreenSize
import com.izyver.gati.presentation.schedule.ScheduleCardAdapter.ScheduleHolder
import com.izyver.gati.presentation.schedule.models.ScheduleImageUI
import com.izyver.gati.utils.stringResBy
import java.util.*
import kotlin.math.abs

class ScheduleCardAdapter : RecyclerView.Adapter<ScheduleHolder>() {

    private var schedules: List<ScheduleImageUI> = arrayListOf()
    var onScheduleClick: OnScheduleClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, null)
        return ScheduleHolder(view)
    }

    override fun getItemCount(): Int = schedules.size

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        holder.bind(schedules[position], onScheduleClick)
    }

    fun setValues(schedules: List<ScheduleImageUI>) {
        this.schedules = schedules
        notifyDataSetChanged()
    }

    class ScheduleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.cardItemTitle)
        private val scheduleImage: ImageView = itemView.findViewById(R.id.cardItemScheduleImage)
        private val markerOldSchedule: TextView = itemView.findViewById(R.id.markerOldSchedule)
        private val imageNotExistFoundText: TextView = itemView.findViewById(R.id.imageNotExistFoundText)

        @SuppressLint("ClickableViewAccessibility")
        internal fun bind(schedule: ScheduleImageUI, onScheduleClick: OnScheduleClickListener?) {
            title.setText(stringResBy(schedule.day))
            markerOldSchedule.visibility = if (schedule.state == OLD) VISIBLE else GONE
            imageNotExistFoundText.visibility = if (schedule.image == null) VISIBLE else GONE
            scheduleImage.setImageBitmap(schedule.image)
            if (onScheduleClick == null) return
            scheduleImage.setOnTouchListener(OnScheduleTouchListener(onScheduleClick, this::getAdapterPosition))
        }
    }

    private class OnScheduleTouchListener(
            private val scheduleClickListener: OnScheduleClickListener,
            private val getPosition: () -> Int
    ) : View.OnTouchListener {

        private var firstTouchedTime: Long = 0
        private var x = 0f
        private var y = 0f
        private var isActionMoved = false

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> onDown(v, event)
                MotionEvent.ACTION_MOVE -> onMove(v, event)
                MotionEvent.ACTION_UP -> onUp(v, event)
                else -> false
            }
        }

        private fun onDown(v: View, event: MotionEvent): Boolean {
            isActionMoved = false
            firstTouchedTime = System.currentTimeMillis()
            x = event.x
            y = event.y
            addLongClickToExecuting(v)
            return true
        }

        private fun onMove(v: View, event: MotionEvent): Boolean {
            val moveX = abs(event.x - x)
            val moveY = abs(event.y - y)
            if (moveX == 0F && moveY == 0F) {
                return true
            }
            stopLongClickExecuting(v)

            isActionMoved = true
            return false
        }

        private fun onUp(v: View, event: MotionEvent): Boolean {
            stopLongClickExecuting(v)
            if (isActionMoved) return false
            val currentTime = System.currentTimeMillis()
            val diffTime = currentTime - firstTouchedTime

            return when {
                diffTime < TIME_LONG_CLICK / 3 -> doShortClick(v)
                diffTime < TIME_LONG_CLICK -> doLongClick(v)
                else -> false
            }
        }

        private fun doLongClick(v: View): Boolean {
            scheduleClickListener.onLongClick(v, getPosition(), x, y)
            return true
        }

        private fun doShortClick(v: View): Boolean {
            scheduleClickListener.onShortClick(v, getPosition())
            return true
        }


        private var executeLongClick: Runnable? = null
        private fun addLongClickToExecuting(v: View) {
            stopLongClickExecuting(v)
            executeLongClick = ExecuteLongClick { doLongClick(v) }
            v.postDelayed(executeLongClick, TIME_LONG_CLICK)
        }

        private fun stopLongClickExecuting(v: View) {
            v.removeCallbacks(executeLongClick)
            executeLongClick = null
        }

        private class ExecuteLongClick(private val funLongClick: () -> Unit) : TimerTask() {
            override fun run() {
                funLongClick()
            }
        }

        companion object {
            private const val TIME_LONG_CLICK = 1000L
        }
    }
}