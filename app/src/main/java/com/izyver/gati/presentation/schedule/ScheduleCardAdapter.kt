package com.izyver.gati.presentation.schedule

import android.annotation.SuppressLint
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
import com.izyver.gati.presentation.schedule.ScheduleCardAdapter.ScheduleHolder
import com.izyver.gati.presentation.schedule.models.ScheduleImageUI
import com.izyver.gati.utils.stringResBy

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

        internal fun bind(schedule: ScheduleImageUI, onScheduleClick: OnScheduleClickListener?) {
            title.setText(stringResBy(schedule.day))
            markerOldSchedule.visibility = if (schedule.state == OLD) VISIBLE else GONE
            imageNotExistFoundText.visibility = if (schedule.image == null) VISIBLE else GONE
            scheduleImage.setImageBitmap(schedule.image)

            setListenerToImage(onScheduleClick)
        }

        @SuppressLint("ClickableViewAccessibility")
        private fun setListenerToImage(onScheduleClick: OnScheduleClickListener?) {
            val touch: OnScheduleTouchListener? = OnScheduleTouchListener()
            scheduleImage.setOnTouchListener(touch)
            scheduleImage.setOnLongClickListener {
                onScheduleClick?.onLongClick(it, adapterPosition, touch?.x ?: 0F, touch?.y ?: 0F) ?: false
            }
            scheduleImage.setOnClickListener { onScheduleClick?.onShortClick(it, adapterPosition) }
        }
    }

    private class OnScheduleTouchListener : View.OnTouchListener {
        var x = 0f
        var y = 0f

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            x = event.x
            y = event.y
            return false
        }
    }
}