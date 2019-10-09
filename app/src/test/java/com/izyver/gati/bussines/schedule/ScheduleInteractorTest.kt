package com.izyver.gati.bussines.schedule

import com.izyver.gati.bussines.SCHEDULE_TYPE_DAYTIME
import com.izyver.gati.bussines.models.Days
import com.izyver.gati.bussines.models.ScheduleImageDto
import com.izyver.gati.bussines.schedule.usecases.DateUseCaseWeekBased
import com.izyver.gati.data.database.schedule.ILocalScheduleDataSource
import com.izyver.gati.data.database.schedule.models.ScheduleDbDto
import com.izyver.gati.data.database.schedule.models.ScheduleDbDtoWithoutBitmap
import com.izyver.gati.data.network.IRemoteScheduleDataSource
import com.izyver.gati.data.network.models.ScheduleNetworkDto
import com.izyver.gati.utils.DATE_PATTERN_STANDARD
import com.izyver.gati.utils.formatStandardGatiDate
import com.izyver.gati.utils.parseStandardGatiDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TITLE_CACHE = "from_cache"
private const val TITLE_STORE = "from_store"

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
        val interactor = ScheduleWeekBasedInteractor(remoteTestSource, localSourceTest, dateUseCase)

        check_network_load_Remote9_Local3(remoteTestSource, localSourceTest, interactor)
        check_network_load_Remote7_Local7(remoteTestSource, localSourceTest, interactor)
        check_network_load_with_feature_schedule(remoteTestSource, localSourceTest, interactor)

        check_network_multiply_load(remoteTestSource, localSourceTest, interactor)
    }

    private suspend fun check_network_load_with_feature_schedule(remoteTestSource: RemoteTestSource,
                                                                 localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
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
                                                          localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
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
                                                          localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
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
                                                    localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
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
    fun loadCacheImage_test() = runBlocking {
        val remoteTestSource = RemoteTestSource()
        val localSourceTest = LocalTestSource()
        val interactor = ScheduleWeekBasedInteractor(remoteTestSource, localSourceTest, dateUseCase)

        check_cache_load_empty_cache(localSourceTest, interactor)
        check_cache_load_not_empty_cache(localSourceTest, interactor)
        check_cache_load_different_cache_and_storage(localSourceTest, interactor)
        check_cache_load_different_and_mixed_cache_and_storage(localSourceTest, interactor)
    }

    private suspend fun check_cache_load_empty_cache(localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4]
        )
        localSourceTest.cachedCalendars = listOf()

        val listOfActualImageDbDto: MutableList<ScheduleImageDto> = ArrayList()
        for (scheduleImageDto in interactor.loadCacheImage()) {
            listOfActualImageDbDto.add(scheduleImageDto)
        }

        Assert.assertEquals(5, listOfActualImageDbDto.size)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[0].name)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[1].name)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[2].name)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[3].name)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[4].name)
    }

    private suspend fun check_cache_load_not_empty_cache(localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4]
        )
        localSourceTest.cachedCalendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4]
        )

        val listOfActualImageDbDto: MutableList<ScheduleImageDto> = ArrayList()
        for (scheduleImageDto in interactor.loadCacheImage()) {
            listOfActualImageDbDto.add(scheduleImageDto)
        }

        Assert.assertEquals(5, listOfActualImageDbDto.size)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[0].name)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[1].name)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[2].name)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[3].name)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[4].name)
    }

    private suspend fun check_cache_load_different_cache_and_storage(localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4]
        )
        localSourceTest.cachedCalendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2]
        )

        val listOfActualImageDbDto: MutableList<ScheduleImageDto> = ArrayList()
        for (scheduleImageDto in interactor.loadCacheImage()) {
            listOfActualImageDbDto.add(scheduleImageDto)
        }

        Assert.assertEquals(5, listOfActualImageDbDto.size)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[0].name)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[1].name)
        Assert.assertEquals(TITLE_CACHE, listOfActualImageDbDto[2].name)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[3].name)
        Assert.assertEquals(TITLE_STORE, listOfActualImageDbDto[4].name)
    }

    private suspend fun check_cache_load_different_and_mixed_cache_and_storage(localSourceTest: LocalTestSource, interactor: ScheduleWeekBasedInteractor) {
        localSourceTest.calendars = listOf(
                calendars[0],
                calendars[1],
                calendars[2],
                calendars[3],
                calendars[4],
                calendars[5],
                calendars[6]
        )
        localSourceTest.cachedCalendars = listOf(
                calendars[0],
                calendars[2],
                calendars[4],
                calendars[6]
        )

        val listOfActualImageDbDto: MutableList<ScheduleImageDto> = ArrayList()
        for (scheduleImageDto in interactor.loadCacheImage()) {
            listOfActualImageDbDto.add(scheduleImageDto)
        }

        Assert.assertEquals(7, listOfActualImageDbDto.size)

        val storList = ArrayList<Any>(3)
        val cacheList = ArrayList<Any>(4)
        for (scheduleImageDto in listOfActualImageDbDto) {
            if (scheduleImageDto.name == TITLE_CACHE) cacheList.add(scheduleImageDto)
            else storList.add(scheduleImageDto)
        }
        Assert.assertEquals(3, storList.size)
        Assert.assertEquals(4, cacheList.size)

    }



    private class RemoteTestSource : IRemoteScheduleDataSource {

        var calendars: List<Calendar> = listOf()

        override suspend fun getExistingSchedule(): List<ScheduleNetworkDto> {
            val list = ArrayList<ScheduleNetworkDto>(calendars.size)
            calendars.forEach { calendar ->
                val simpleDateFormat = SimpleDateFormat(DATE_PATTERN_STANDARD)
                val dateStr = simpleDateFormat.format(calendar.time)
                val day = Days.from(calendar)
                list.add(ScheduleNetworkDto(day.index, "title${day.index}", day.name, SCHEDULE_TYPE_DAYTIME, dateStr))
            }
            return list
        }

        override suspend fun download(schedule: ScheduleNetworkDto): ByteArray? {
            return null
        }

    }

    private class LocalTestSource : ILocalScheduleDataSource {
        override fun getScheduleByDay(day: Days): ScheduleDbDto? {
            return null
        }

        override fun saveSchedule(schedyle: ScheduleNetworkDto, image: ByteArray?) {

        }

        private var list: List<ScheduleDbDto> = listOf()
        private var cachedList: List<ScheduleDbDto> = listOf()

        var calendars: List<Calendar>
            set(value) {
                list = initList(value, TITLE_STORE)
            }
            get() = throw RuntimeException()

        var cachedCalendars: List<Calendar>
            set(value) {
                cachedList = initList(value, TITLE_CACHE)
            }
            get() = throw RuntimeException()

        private fun initList(calendars: List<Calendar>, title: String): List<ScheduleDbDto> {
            val list = ArrayList<ScheduleDbDto>(calendars.size)
            calendars.forEach { calendar ->
                val day = Days.from(calendar)
                list.add(
                        ScheduleDbDto("key${day.index}", day.index, formatStandardGatiDate(calendar.time), SCHEDULE_TYPE_DAYTIME,
                                null, title, day.name))
            }
            return list
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

        override fun getCachedSchedules(): List<ScheduleDbDto> = cachedList

        override fun getScheduleByDate(date: String?): ScheduleDbDto? {
            for (scheduleDbDto in list) {
                if (scheduleDbDto.date == date)
                    return scheduleDbDto
            }
            return null
        }
    }
}