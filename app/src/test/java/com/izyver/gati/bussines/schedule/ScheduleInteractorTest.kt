package com.izyver.gati.bussines.schedule

import android.graphics.Bitmap
import com.izyver.gati.bussines.SCHEDULE_TUPE_API_DAYTIME
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.bussines.schedule.usecases.DateUseCaseWeekBased
import com.izyver.gati.data.database.ILocalScheduleDataSource
import com.izyver.gati.data.database.models.ScheduleDbDto
import com.izyver.gati.data.database.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.data.network.ScheduleNetworkDto
import com.izyver.gati.utils.DATE_PATTERN_STANDARD
import com.izyver.gati.utils.formatStandardGatiDate
import com.izyver.gati.utils.parseStandardGatiDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleInteractorTest {

    // "yyyy-MM-dd HH:mm:ss"

    private val thursday28 = "2019-09-28 00:00:00" //not actual
    private val friday27 = "2019-09-27 00:00:00"//not actual

    private val monday30 = "2019-09-30 00:00:00"//actual
    private val tuesday1 = "2019-10-01 00:00:00"//actual
    private val wednesday2 = "2019-10-02 00:00:00"//actual
    private val thursday3 = "2019-10-03 00:00:00"//actual
    private val friday4 = "2019-10-04 00:00:00"//actual
    private val saturday5 = "2019-10-05 00:00:00"//actual
    private val sunday6 = "2019-10-06 00:00:00" //actual

    private val monday7 = "2019-10-07 00:00:00"//future

    private val today = wednesday2

    private val calendars = listOf<Calendar>(
            Calendar.getInstance().also { it.time = parseStandardGatiDate(thursday28)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(friday27)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(monday30)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(tuesday1)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(wednesday2)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(thursday3)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(friday4)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(saturday5)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(sunday6)!! },
            Calendar.getInstance().also { it.time = parseStandardGatiDate(monday7)!! }
    )
    private val dateUseCase = object : DateUseCaseWeekBased() {
        override fun getCurrentDate(): Date {
            return parseStandardGatiDate(today)!!
        }
    }

    @Test
    fun loadNetworkImages_test() = runBlocking {
        val remoteTestSource = RemoteTestSource()
        val localSourceTest = LocalTestSource()
        val interactor = ScheduleInteractor(remoteTestSource, localSourceTest, dateUseCase)

        check_network_load_Remote9_Local3(remoteTestSource, localSourceTest, interactor)
        check_network_load_Remote7_Local7(remoteTestSource, localSourceTest, interactor)
        check_network_load_with_feature_schedule(remoteTestSource, localSourceTest, interactor)

        check_network_multiply_load(remoteTestSource, localSourceTest, interactor)
    }

    private suspend fun check_network_load_with_feature_schedule(remoteTestSource: RemoteTestSource,
                                                                 localSourceTest: LocalTestSource,
                                                                 interactor: ScheduleInteractor) {
        remoteTestSource.calendars = listOf(
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6],
                calendars[7],
                calendars[8],
                calendars[9]
        )
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6],
                calendars[7],
                calendars[8]
        )

        val actualListFromNetworkImage = ArrayList<ScheduleImageDto>()
        for (networkImage in interactor.loadNetworkImages()) {
            actualListFromNetworkImage.add(networkImage)
        }
        Assert.assertEquals(1, actualListFromNetworkImage.size)
    }

    private suspend fun check_network_load_Remote9_Local3(remoteTestSource: RemoteTestSource,
                                                          localSourceTest: LocalTestSource,
                                                          interactor: ScheduleInteractor) {
        remoteTestSource.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6],
                calendars[7],
                calendars[8]
        )
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2]
        )

        val actualListFromNetworkImage = ArrayList<ScheduleImageDto>()
        for (networkImage in interactor.loadNetworkImages()) {
            actualListFromNetworkImage.add(networkImage)
        }

        Assert.assertEquals(6, actualListFromNetworkImage.size)
        Assert.assertEquals(Days.TUESDAY, actualListFromNetworkImage[0].day)

    }

    private suspend fun check_network_load_Remote7_Local7(remoteTestSource: RemoteTestSource,
                                                          localSourceTest: LocalTestSource,
                                                          interactor: ScheduleInteractor) {
        remoteTestSource.calendars = listOf(
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6],
                calendars[7],
                calendars[8]
        )
        localSourceTest.calendars = listOf(
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6],
                calendars[7],
                calendars[8]
        )

        val actualListFromNetworkImage = ArrayList<ScheduleImageDto>()
        for (networkImage in interactor.loadNetworkImages()) {
            actualListFromNetworkImage.add(networkImage)
        }

        Assert.assertEquals(0, actualListFromNetworkImage.size)
    }

    private suspend fun check_network_multiply_load(remoteTestSource: RemoteTestSource,
                                                    localSourceTest: LocalTestSource,
                                                    interactor: ScheduleInteractor) {
        remoteTestSource.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6],
                calendars[7],
                calendars[8]
        )
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2]
        )
        val actualListFromNetworkImage = ArrayList<ScheduleImageDto>()
        for (networkImage in interactor.loadNetworkImages()) {
            actualListFromNetworkImage.add(networkImage)
            break
        }
        for (networkImage in interactor.loadNetworkImages()) {
            actualListFromNetworkImage.add(networkImage)
            break
        }
        for (networkImage in interactor.loadNetworkImages()) {
            actualListFromNetworkImage.add(networkImage)
        }

        Assert.assertEquals(6, actualListFromNetworkImage.size)
    }


    @Test
    fun loadCacheImage_test() {

    }


    private class RemoteTestSource : IRemoteScheduleDataSource {

        var calendars: List<Calendar> = listOf()

        override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
            var list = ArrayList<ScheduleNetworkDto>(calendars.size)
            calendars.forEach { calendar ->
                val simpleDateFormat = SimpleDateFormat(DATE_PATTERN_STANDARD)
                val dateStr = simpleDateFormat.format(calendar.time)
                val day = Days.from(calendar)
                list.add(ScheduleNetworkDto(day.index, "title${day.index}", day.name, SCHEDULE_TUPE_API_DAYTIME, dateStr))
            }
            return list
        }

        override suspend fun getBitmapBy(schedule: ScheduleNetworkDto): Bitmap? {
            return null
        }

    }

    private class LocalTestSource : ILocalScheduleDataSource {

        var list: List<ScheduleDbDto> = listOf()

        var calendars: List<Calendar> = listOf()
            set(value) {
                initList(value)
            }

        private fun initList(calendars: List<Calendar>) {
            val list = ArrayList<ScheduleDbDto>(calendars.size)
            calendars.forEach { calendar ->
                val day = Days.from(calendar)
                list.add(
                        ScheduleDbDto("key${day.index}", day.index, formatStandardGatiDate(calendar.time), SCHEDULE_TUPE_API_DAYTIME,
                                null, "title${day.index}", day.name))
            }
            this.list = list
        }

        override fun getScheduleDescription(): List<ScheduleDbDtoWithoutBitmap> {
            val newList = ArrayList<ScheduleDbDtoWithoutBitmap>(6)
            list.forEach {
                newList.add(ScheduleDbDtoWithoutBitmap(
                        it.key, it.id, it.date, it.type, it.title, it.dayWeek))

            }
            return newList
        }

        override fun getStoredSchedule(): List<ScheduleDbDto> = list

        override fun getCachedSchedules(): List<ScheduleDbDto> = listOf()
    }
}