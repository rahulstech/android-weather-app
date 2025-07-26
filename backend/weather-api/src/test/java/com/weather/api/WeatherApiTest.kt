package com.weather.api

import com.weather.api.model.Astronomy
import com.weather.api.model.Condition
import com.weather.api.model.CurrentWeather
import com.weather.api.model.ErrorResponse
import com.weather.api.model.ForecastDay
import com.weather.api.model.Location
import com.weather.api.model.WeatherDay
import com.weather.api.model.WeatherHour
import com.weather.api.util.parseErrorBody
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WeatherApiTest {

    var _server: MockWebServer? = null
    val server: MockWebServer get() = _server!!
    var _client: WeatherClient? = null
    val client: WeatherClient get() = _client!!
    var _api: WeatherApi? = null
    val api: WeatherApi get() = _api!!

    @Before
    fun setup() {
        _server = MockWebServer()
        _client = WeatherClient("FAKE_API_KEy", server.url("/").toString())
        _api = client.api
    }

    @After
    fun teardown() {
        _api = null
        _client = null
        _server?.shutdown()
        _server = null
    }

    @Test
    fun testSearchCity() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                  {
                    "id": 2796590,
                    "name": "Holborn",
                    "region": "Camden Greater London",
                    "country": "United Kingdom",
                    "lat": 51.52,
                    "lon": -0.12,
                    "url": "holborn-camden-greater-london-united-kingdom"
                  }
                ]
            """.trimIndent())
        server.enqueue(mockResponse)

        val response = api.searchCity("Holborn")
        val actual = response.body()
        val expect = listOf(
            Location("2796590", "Holborn", "United Kingdom", 51.52f, -0.12f)
        )

        assertEquals(expect,actual)
    }

    @Test
    fun testGetForecast() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "location": {
                        "name": "London",
                        "region": "City of London, Greater London",
                        "country": "United Kingdom",
                        "lat": 51.5171,
                        "lon": -0.1062,
                        "tz_id": "Europe/London",
                        "localtime_epoch": 1752413525,
                        "localtime": "2025-07-13 14:32"
                    },
                    "current": {
                        "last_updated_epoch": 1752413400,
                        "last_updated": "2025-07-13 14:30",
                        "temp_c": 27.4,
                        "temp_f": 81.3,
                        "is_day": 1,
                        "condition": {
                            "text": "Partly cloudy",
                            "icon": "//cdn.weatherapi.com/weather/64x64/day/116.png",
                            "code": 1003
                        },
                        "wind_mph": 2.7,
                        "wind_kph": 4.3,
                        "wind_degree": 98,
                        "wind_dir": "E",
                        "pressure_mb": 1012.0,
                        "pressure_in": 29.88,
                        "precip_mm": 0.0,
                        "precip_in": 0.0,
                        "humidity": 48,
                        "cloud": 50,
                        "feelslike_c": 26.6,
                        "feelslike_f": 79.8,
                        "windchill_c": 29.6,
                        "windchill_f": 85.3,
                        "heatindex_c": 28.7,
                        "heatindex_f": 83.6,
                        "dewpoint_c": 10.9,
                        "dewpoint_f": 51.6,
                        "vis_km": 10.0,
                        "vis_miles": 6.0,
                        "uv": 6.5,
                        "gust_mph": 3.1,
                        "gust_kph": 5.0
                    },
                    "forecast": {
                        "forecastday": [
                            {
                                "date": "2025-07-14",
                                "date_epoch": 1752451200,
                                "day": {
                                    "maxtemp_c": 27.2,
                                    "maxtemp_f": 80.9,
                                    "mintemp_c": 17.1,
                                    "mintemp_f": 62.8,
                                    "avgtemp_c": 22.0,
                                    "avgtemp_f": 71.6,
                                    "maxwind_mph": 17.4,
                                    "maxwind_kph": 28.1,
                                    "totalprecip_mm": 0.0,
                                    "totalprecip_in": 0.0,
                                    "totalsnow_cm": 0.0,
                                    "avgvis_km": 10.0,
                                    "avgvis_miles": 6.0,
                                    "avghumidity": 46,
                                    "daily_will_it_rain": 0,
                                    "daily_chance_of_rain": 0,
                                    "daily_will_it_snow": 0,
                                    "daily_chance_of_snow": 0,
                                    "condition": {
                                        "text": "Sunny",
                                        "icon": "//cdn.weatherapi.com/weather/64x64/day/113.png",
                                        "code": 1000
                                    },
                                    "uv": 1.8
                                },
                                "astro": {
                                    "sunrise": "05:00 AM",
                                    "sunset": "09:12 PM",
                                    "moonrise": "11:04 PM",
                                    "moonset": "09:01 AM",
                                    "moon_phase": "Waning Gibbous",
                                    "moon_illumination": 89,
                                    "is_moon_up": 1,
                                    "is_sun_up": 0
                                },
                                "hour": [
                                    {
                                        "time_epoch": 1752447600,
                                        "time": "2025-07-14 00:00",
                                        "temp_c": 20.9,
                                        "temp_f": 69.7,
                                        "is_day": 0,
                                        "condition": {
                                            "text": "Partly Cloudy ",
                                            "icon": "//cdn.weatherapi.com/weather/64x64/night/116.png",
                                            "code": 1003
                                        },
                                        "wind_mph": 4.9,
                                        "wind_kph": 7.9,
                                        "wind_degree": 174,
                                        "wind_dir": "S",
                                        "pressure_mb": 1011.0,
                                        "pressure_in": 29.86,
                                        "precip_mm": 0.0,
                                        "precip_in": 0.0,
                                        "snow_cm": 0.0,
                                        "humidity": 45,
                                        "cloud": 34,
                                        "feelslike_c": 20.9,
                                        "feelslike_f": 69.7,
                                        "windchill_c": 20.9,
                                        "windchill_f": 69.7,
                                        "heatindex_c": 20.9,
                                        "heatindex_f": 69.7,
                                        "dewpoint_c": 8.7,
                                        "dewpoint_f": 47.7,
                                        "will_it_rain": 0,
                                        "chance_of_rain": 0,
                                        "will_it_snow": 0,
                                        "chance_of_snow": 0,
                                        "vis_km": 10.0,
                                        "vis_miles": 6.0,
                                        "gust_mph": 7.5,
                                        "gust_kph": 12.1,
                                        "uv": 0
                                    },
                                    {
                                        "time_epoch": 1752451200,
                                        "time": "2025-07-14 01:00",
                                        "temp_c": 20.5,
                                        "temp_f": 68.9,
                                        "is_day": 0,
                                        "condition": {
                                            "text": "Partly Cloudy ",
                                            "icon": "//cdn.weatherapi.com/weather/64x64/night/116.png",
                                            "code": 1003
                                        },
                                        "wind_mph": 5.6,
                                        "wind_kph": 9.0,
                                        "wind_degree": 180,
                                        "wind_dir": "S",
                                        "pressure_mb": 1011.0,
                                        "pressure_in": 29.85,
                                        "precip_mm": 0.0,
                                        "precip_in": 0.0,
                                        "snow_cm": 0.0,
                                        "humidity": 47,
                                        "cloud": 50,
                                        "feelslike_c": 20.5,
                                        "feelslike_f": 68.9,
                                        "windchill_c": 20.5,
                                        "windchill_f": 68.9,
                                        "heatindex_c": 20.5,
                                        "heatindex_f": 68.9,
                                        "dewpoint_c": 8.8,
                                        "dewpoint_f": 47.9,
                                        "will_it_rain": 0,
                                        "chance_of_rain": 0,
                                        "will_it_snow": 0,
                                        "chance_of_snow": 0,
                                        "vis_km": 10.0,
                                        "vis_miles": 6.0,
                                        "gust_mph": 8.6,
                                        "gust_kph": 13.8,
                                        "uv": 0
                                    }
                                ]
                            }
                        ]
                    }
                }
            """.trimIndent())
        server.enqueue(mockResponse)

        val response = api.getForecast("London", 1)
        val actual = response.body()?.forecast?.forecastday
        val expected = listOf(
            ForecastDay(
                LocalDate.of(2025,7,14),
                WeatherDay(27.2f,17.1f, 0f,0f, 46, Condition("//cdn.weatherapi.com/weather/64x64/day/113.png",1000),1.8f),
                Astronomy(LocalTime.of(5,0), LocalTime.of(21,12)),
                listOf(
                    WeatherHour(LocalDateTime.of(2025,7,14,0,0), 20.9f, false, Condition("//cdn.weatherapi.com/weather/64x64/night/116.png",1003), 20.9f, false, false, 0f),
                    WeatherHour(LocalDateTime.of(2025,7,14,1,0), 20.5f, false, Condition("//cdn.weatherapi.com/weather/64x64/night/116.png",1003), 20.5f, false, false, 0f),
                    )
            )
        )

        assertEquals(expected, actual)
    }


//    @Test
//    fun testErrorResponse() = runBlocking {
//        val mockResponse = MockResponse()
//            .setResponseCode(400)
//            .setBody("""
//                {
//                  "code": 1003,
//                  "message": "Parameter 'q' not provided."
//                }
//            """.trimIndent())
//        server.enqueue(mockResponse)
//
//        val response = api.getCurrentWeather("")
//        val actual = response.parseErrorBody()
//        val expected = ErrorResponse(1003, "Parameter 'q' not provided.")
//        assertEquals(expected,actual)
//    }
}