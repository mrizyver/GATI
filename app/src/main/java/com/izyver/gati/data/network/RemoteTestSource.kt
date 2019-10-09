package com.izyver.gati.data.network

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.izyver.gati.R
import com.izyver.gati.bussines.SCHEDULE_TYPE_DAYTIME
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.data.network.models.ScheduleNetworkDto
import com.izyver.gati.utils.DATE_PATTERN_STANDARD
import com.izyver.gati.utils.textAsBitmap
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RemoteTestSource : IRemoteScheduleDataSource {

    private val TAG = "RemoteTestSource"

    override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
        delay(2000)
        val list = ArrayList<ScheduleNetworkDto>(6)
        repeat(6) { index ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, index + 2)
            val simpleDateFormat = SimpleDateFormat(DATE_PATTERN_STANDARD)
            val dateStr = simpleDateFormat.format(calendar.time)
            list.add(ScheduleNetworkDto(index, "title$index", Days.from(calendar).name, SCHEDULE_TYPE_DAYTIME, dateStr))
        }
        return list
    }

    override suspend fun download(schedule: ScheduleNetworkDto): ByteArray? {
        delay(1500)
        Log.d(TAG, "bitmap downloaded fro $schedule")
        val bitmap = textAsBitmap("${schedule.image} from network", 150F, Color.BLACK)
        val buffer = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer)
        return buffer.toByteArray()
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