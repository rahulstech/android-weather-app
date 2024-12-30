package rahulstech.weather.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.weather.api.Location
import com.weather.api.WeatherClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherRepository(private val apiKey: String) {

    private val TAG = WeatherRepository::class.java.simpleName

    private val FORMATER_TIME = DateTimeFormatter.ofPattern("hh:mm a")

    private val FORMATER_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    private val api by lazy { WeatherClient.getInstance(apiKey).api }

    fun getWeatherToday(locationId: String): LiveData<WeatherForecast?> {
        val mainFlow: Flow<WeatherForecast?> = flow {
            val response = api.getForecast("id:$locationId", 1)
            if (response.isSuccessful) {
                emit(response.body())
            }
            else {
                // TODO: handle request error
                Log.e(TAG, "error = ${response.errorBody()}")
            }
        }
            .transform {

                if (null == it) {
                    emit(null)
                    return@transform
                }

                val location = it.location
                val forecast = it.forecast.forecastday[0]
                val hours = forecast.hour
                val day = forecast.day
                val astronomy = forecast.astro

                val city = convertLocationToCity(locationId, location)

                val date = LocalDate.now()

                val dailyWeather = DailyWeather(city, date,
                        day.maxtemp_c,day.mintemp_c,day.avgtmp_c,
                        day.totalprecip_mm, day.avghumidity,day.uv,
                        LocalTime.parse(astronomy.sunrise,FORMATER_TIME), LocalTime.parse(astronomy.sunset,FORMATER_TIME))

                val hourlyWeathers = mutableListOf<HourlyWeather>()
                for (data in hours) {
                    val hourlyWeather = HourlyWeather(city, LocalDateTime.parse(data.time, FORMATER_DATE_TIME), 1 == data.is_day,
                        WeatherCondition.get(data.condition.code),
                        data.temp_c, data.feelslike_c, data.precip_mm, data.humidity, data.uv)
                    hourlyWeathers.add(hourlyWeather)
                }

                val weatherForecast = WeatherForecast(city,dailyWeather,hourlyWeathers)

                emit(weatherForecast)
            }
            .flowOn(Dispatchers.IO)
            .catch {
                // TODO: handle flow error
                Log.e(TAG, null,it)
            }

        return mainFlow.asLiveData()
    }

    fun searchCity(keyword: String?): LiveData<List<City>> {
        val result = MutableLiveData<List<City>>(emptyList())
        if (keyword.isNullOrBlank()) {
            return result
        }

        val mainFlow: Flow<List<City>> = flow {
            val res = api.searchCity(keyword)
            if (res.isSuccessful) {
                emit(res.body())
            }
            else {
                Log.i(TAG, "city search request error ${res.errorBody()}")
                emit(emptyList())
            }
        }
            .transform {
                if (it.isNullOrEmpty()) {
                    emit(emptyList())
                }
                else {
                    val cities = mutableListOf<City>()
                    for (location in it) {
                        val city = convertLocationToCity(location.id, location)
                        cities.add(city)
                    }
                    emit(cities)
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { Log.e(TAG, null, it) }

        GlobalScope.launch {
            mainFlow.collect { result.postValue(it) }
        }

        return result
    }

    private fun convertLocationToCity(locationId: String, location: Location): City {
        val zoneId = if (location.tz_id.isNullOrBlank()) null else ZoneId.of(location.tz_id)
        return City(
            0, locationId, location.name, location.region, location.country,
            location.lat, location.lon, zoneId
        )
    }
}