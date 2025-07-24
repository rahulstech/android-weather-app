package rahulstech.weather.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import rahulstech.weather.database.WeatherDB
import rahulstech.weather.database.entity.City
import rahulstech.weather.database.helper.createInMemoryWeatherDB
import rahulstech.weather.database.helper.getFakeDataForVersion

@RunWith(AndroidJUnit4::class)
class CityDaoTest {

    lateinit var db: WeatherDB
    lateinit var dao: CityDao

    @Before
    fun setup() {
        val fakeData = getFakeDataForVersion()
        db = createInMemoryWeatherDB(fakeData)
        dao = db.getCityDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testAddCity() {
        val city = City(0, "Kolkata", "India", 93.456f, 12.265f, "22156")
        val actual = dao.addCity(city)
        assertEquals(3, actual)
    }

    @Test
    fun testGetAllCities() {
        val expected = listOf(
            City(1L, "London", "United Kingdom", 12.563f, 0.5645f, "12356"),
            City(2L, "New York", "USA", 56.563f, 23.5645f, "58774")
        )

        val actual = dao.getAllCities()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetCityById() {
        val expected = City(1L, "London", "United Kingdom", 12.563f, 0.5645f, "12356")
        val actual = dao.getCityById(1)
        assertEquals(expected, actual)
    }

    @Test
    fun testGetCityById_Unknown() {
        val expected = null
        val actual = dao.getCityById(3)
        assertEquals(expected, actual)
    }
}