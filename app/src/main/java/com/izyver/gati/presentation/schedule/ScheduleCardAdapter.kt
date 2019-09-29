package com.izyver.gati.presentation.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.izyver.gati.R
import com.izyver.gati.presentation.schedule.ScheduleCardAdapter.ScheduleHolder
import com.izyver.gati.presentation.schedule.models.ScheduleImageUI
import com.izyver.gati.utils.stringResBy

class ScheduleCardAdapter : RecyclerView.Adapter<ScheduleHolder>() {

    private var schedules: List<ScheduleImageUI> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, null)
        return ScheduleHolder(view)
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        holder.bind(schedules[position])
    }

    fun setValues(schedules: List<ScheduleImageUI>){
        this.schedules = schedules
    }

    class ScheduleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val scheduleImage: ImageView = itemView.findViewById(R.id.cardItemscheduleImage)
        private val title: TextView = itemView.findViewById(R.id.cardItemTitle)

        internal fun bind(schedule: ScheduleImageUI){
            title.setText(stringResBy(schedule.day))
            scheduleImage.setImageBitmap(schedule.image)
        }
    }
}