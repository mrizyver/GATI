package com.izyver.gati.data.network

import android.graphics.Bitmap
import android.graphics.Color
import com.izyver.gati.R
import com.izyver.gati.bussines.PATTERN_DATE
import com.izyver.gati.bussines.SCHEDULE_TUPE_API_DAYTIME
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.utils.textAsBitmap
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RemoteTestSource : IRemoteScheduleDataSource {

    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        delay(2000)
        val list = ArrayList<ScheduleNetworkDto>(6)
        repeat(6) { index ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, index + 2)
            val simpleDateFormat = SimpleDateFormat(PATTERN_DATE)
            val dateStr = simpleDateFormat.format(calendar.time)
            list.add(ScheduleNetworkDto(index, "title$index", Days.from(calendar).name, SCHEDULE_TUPE_API_DAYTIME, dateStr))
        }
        return list
    }

    override suspend fun getBitmapBy(schedule: ScheduleNetworkDto): Bitmap {
        delay(1500)
        return textAsBitmap("${schedule.image} from network", 150F, Color.BLACK)
    }


    private fun createBitmap(day: Days): Bitmap? {
        return when (day) {
            Days.MONDAY -> getBitmap(R.drawable.mon)
            Days.TUESDAY -> getBitmap(R.drawable.tue)
            Days.WEDNESDAY -> getBitmap(R.drawable.wed)
            Days.THURSDAY -> getBitmap(R.drawable.thu)
            Days.FRIDAY -> getBitmap(R.drawable.fri)
            Days.SATURDAY -> getBitmap(R.drawable.sat)
            Days.SUNDAY -> getBitmap(R.drawable.sun)
        }
    }

    private fun getBitmap(id: Int): Bitmap? {
        return try {
            Picasso.get().load(id).get()
        } catch (e: IOException) {
            null
        } catch (e: NullPointerException) {
            null
        }
    }
}