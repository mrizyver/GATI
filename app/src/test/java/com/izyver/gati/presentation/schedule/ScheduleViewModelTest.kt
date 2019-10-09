package com.izyver.gati.presentation.schedule

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.bussines.schedule.IScheduleInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mock

class ScheduleViewModelTest {

    @Mock
    lateinit var scheduleImageDto: ScheduleImageDto

    val testInteractor = object : IScheduleInteractor {
        override fun loadNetworkImages(): Channel<ScheduleImageDto> {
            val channel = Channel<ScheduleImageDto>()

            runBlocking {
                repeat(7) {
                    channel.send(scheduleImageDto)
                }
            }
            return channel
        }

        override fun loadCacheImage(): Channel<ScheduleImageDto> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSchedule(day: Days): ByteArray? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    @Mock
    lateinit var lifecycleOwner: LifecycleOwner

    @Test
    fun loadImageTest() {

        val model = ScheduleViewModel(testInteractor)

        model.scheduleImage.observe(lifecycleOwner, Observer {  })



    }

}