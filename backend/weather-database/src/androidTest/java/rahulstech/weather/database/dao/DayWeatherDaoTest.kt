package rahulstech.weather.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rahulstech.weather.database.WeatherDB
import rahulstech.weather.database.entity.DayWeather
import rahulstech.weather.database.helper.createInMemoryWeatherDB
import rahulstech.weather.database.helper.getFakeDataForVersion
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class DayWeatherDaoTest {

    lateinit var db: WeatherDB

    lateinit var dao: DayWeatherDao

    @Before
    fun setup() {
        val fakeData = getFakeDataForVersion()
        db = createInMemoryWeatherDB(fakeData)
        dao = db.getDayWeatherDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testAddDayWeather() {
        val weather = DayWeather(
            0, 1L, LocalDate.of(2025,7,2), 1000, 113,
            26f, 20f, 41, 0.1f, 0.25f, 0f,
            LocalTime.of(5,52), LocalTime.of(18, 48), LocalDateTime.of(2025,7,2,0,0)
        )
        val actual = dao.addDayWeather(weather)
        assertEquals(3,actual)

    }

    @Test
    fun testAddMultipleDayWeather() {
        val data = listOf(
            DayWeather(
            0, 1L, LocalDate.of(2025,7,2), 1000, 113,
            26f, 20f, 41, 0.1f, 0.25f, 0f,
            LocalTime.of(5,52), LocalTime.of(18, 48), LocalDateTime.of(2025,7,2,0,0)),

            DayWeather(
                0, 1L, LocalDate.of(2025,7,3), 1000, 113,
                27f, 21f, 40, 0.2f, 0.27f, 0f,
                LocalTime.of(5,55), LocalTime.of(18, 50), LocalDateTime.of(2025,7,2,0,0),)
        )
        val actual = dao.addMultipleDayWeather(data)
        val expected = listOf<Long>(3,4)
        assertEquals(expected,actual)



    }

    @Test
    fun testGetCityWeatherBetweenDates() {
        val expected = listOf(
           DayWeather(1L, 1L, LocalDate.of(2025,7,1),
               1000, 113, 28f, 23f, 43, 0.2f, 0.20f, 0f,
               LocalTime.of(5,50), LocalTime.of(18,50), LocalDateTime.of(2025,6,30,0,0)),
            DayWeather(2L, 1L, LocalDate.of(2025, 7, 2),
                1002, 119, 26f, 22f, 40, 0.3f, 0.30f, 0f,
                LocalTime.of(5, 51), LocalTime.of(18, 49),LocalDateTime.of(2025,7,2,0,0))
        )

        val actual = dao.getCityWeatherBetweenDates(1, LocalDate.of(2025,7,1), LocalDate.of(2025,7,2))

        assertEquals(expected, actual)
    }

    @Test
    fun testUpdateDayWeather() {
        val weather = DayWeather(1L, 1L, LocalDate.of(2025,7,1),
            1000, 113, 28f, 23f, 43, 0.2f, 0.20f, 0f,
            LocalTime.of(5,50), LocalTime.of(18,50), LocalDateTime.of(2025,6,7,1,12,0))
        val actual = dao.updateDayWeather(weather)
        assertEquals(1, actual)
    }
}