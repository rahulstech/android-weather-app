package rahulstech.weather.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.CurrentWeatherModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import rahulstech.weather.repository.util.Resource
import java.time.LocalDate
import java.time.LocalDateTime

interface WeatherRepository {

    suspend fun searchCity(keyword: String?): Flow<Resource<List<CityModel>>>

    suspend fun saveCity(city: CityModel): Flow<Resource<CityModel>>

    suspend fun getHourlyForecast(cityId: Long, date: LocalDate): Flow<Resource<List<HourWeatherModel>>>

    suspend fun getForecastForDate(cityId: Long, date: LocalDate): Flow<Resource<DayWeatherModel>>

    suspend fun getForecast(cityId: Long): Flow<Resource<List<DayWeatherModel>>>

    suspend fun findCityById(cityId: Long): Flow<Resource<CityModel>>
}