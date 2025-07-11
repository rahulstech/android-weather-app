package rahulstech.weather.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rahulstech.weather.database.WeatherDB
import rahulstech.weather.database.entity.HourWeather
import rahulstech.weather.database.helper.createInMemoryWeatherDB
import rahulstech.weather.database.helper.getFakeDataForVersion
import rahulstech.weather.database.helper.testLiveData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class HourWeatherDaoTest {

    lateinit var db: WeatherDB
    lateinit var dao: HourWeatherDao

    @Before
    fun setup() {
        val fakeData = getFakeDataForVersion()
        db = createInMemoryWeatherDB(fakeData)
        dao = db.getHourWeatherDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testAddHourWeather() {
        val hourWeather = HourWeather(0, 1L, LocalDate.of(2025, 7, 1), LocalTime.of(9, 0),
            true, LocalDateTime.of(2025, 7, 1, 0, 0),
            1000, 113, 24f, 26f, 0.2f, true, false)
        val actual = dao.addHourWeather(hourWeather)
        assertEquals(3, actual)
    }

    @Test
    fun testUpdateHourWeather() {
        val hourWeather = HourWeather(1L, 1L,
            LocalDate.of(2025, 7, 1), LocalTime.of(8, 0), true,
            LocalDateTime.of(2025, 7, 1, 8, 15),
            1000, 113, 23f, 25f, 0.2f, true)

        val actual = dao.updateHourWeather(hourWeather)
        assertEquals(1, actual)

    }

    @Test
    fun testGetCityHourlyWeatherForHour() {
        val liveDate = dao.getCityHourlyWeatherForHour(1, LocalDate.of(2025,7,1), LocalTime.of(8,0))

        val expected = HourWeather(1L, 1L,
            LocalDate.of(2025, 7, 1), LocalTime.of(8, 0), true,
            LocalDateTime.of(2025, 7, 1, 0, 0),
            1000, 113, 23f, 25f, 0.2f)

        testLiveData(liveDate) { actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getCityHourlyWeatherForWholeDay() {
        val liveDate = dao.getCityHourlyWeatherForWholeDay(1, LocalDate.of(2025,7,1))
        val expected = listOf(
            HourWeather(1L, 1L,
                LocalDate.of(2025, 7, 1), LocalTime.of(8, 0), true,
                LocalDateTime.of(2025, 7, 1, 0, 0),
                1000, 113, 23f, 25f, 0.2f),
            HourWeather(2L, 1L,
                LocalDate.of(2025, 7, 1), LocalTime.of(12, 0), true,
                LocalDateTime.of(2025, 7, 1, 0, 0),
                1001, 115, 25f, 27f, 0.3f)
        )

        testLiveData(liveDate) { actual ->
            assertEquals(expected, actual)
        }
    }
}