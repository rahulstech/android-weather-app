package rahulstech.weather.repository.util

import com.weather.api.model.ForecastDay
import com.weather.api.model.Location
import com.weather.api.model.WeatherHour
import rahulstech.weather.database.entity.City
import rahulstech.weather.database.entity.DayWeather
import rahulstech.weather.database.entity.HourWeather
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import java.time.LocalDateTime
import kotlin.math.floor

internal fun Location.toCity(cityId: Long = 0): City {
    return City(cityId, name, country, lat, lon, id)
}

internal fun List<Location>.toCitiesList(): List<City> = map { location -> location.toCity() }


internal fun Location.toCityModel(): CityModel = CityModel(0, name, country, lat, lon, id)

internal fun List<Location>.toCityModelList(): List<CityModel> = map(Location::toCityModel)


internal fun CityModel.toCity(cityId: Long = id): City = City(cityId, name, country, longitude, latitude, remoteId)

internal fun City.toCityModel(): CityModel = CityModel(id, name, country, longitude, latitude, remoteId)

@JvmName("cityListToCityModelList")
internal fun List<City>.toCityModelList(): List<CityModel> = map { city -> city.toCityModel() }

internal fun DayWeather.toDayWeatherModel(): DayWeatherModel {
    return DayWeatherModel(id, cityId, date, conditionCode, iconCode,
        floor(temperatureMaxC).toInt(), floor(temperatureMinC).toInt(), humidity, uv, precipitationMm, snowCm,
        sunrise, sunset, lastModified)
}

internal fun List<DayWeather>.toDayWeatherModelList(): List<DayWeatherModel> = map { dayWeather -> dayWeather.toDayWeatherModel() }

internal fun ForecastDay.toDayWeather(cityId: Long): DayWeather {
    return DayWeather(0, cityId, date, day.condition.code, day.condition.iconCode,
        day.maxtemp_c, day.mintemp_c, day.avghumidity, day.uv, day.totalprecip_mm, day.totalsnow_cm,
        astro.sunrise, astro.sunset)
}

internal fun List<ForecastDay>.toDayWeatherList(cityId: Long = 0): List<DayWeather> = map { it.toDayWeather(cityId) }

internal fun WeatherHour.toHourWeather(cityId: Long): HourWeather {
    return HourWeather(0, cityId, time.toLocalDate(), time.toLocalTime(), is_day, LocalDateTime.now(),
        condition.code, condition.iconCode, temp_c, feelslike_c, uv, will_it_rain, will_it_snow)
}

@JvmName("weatherHourListToHourWeatherList")
internal fun List<WeatherHour>.toHourWeatherList(cityId: Long = 0): List<HourWeather> = map { it.toHourWeather(cityId) }

@JvmName("forecastDayToHourWeatherList")
internal fun List<ForecastDay>.toHourWeatherList(cityId: Long = 0): List<HourWeather> = flatMap { forecastDay -> forecastDay.hour.toHourWeatherList(cityId) }

internal fun HourWeather.toHourWeatherModel(): HourWeatherModel {
    val datetime = LocalDateTime.of(date, time)
    return HourWeatherModel(id, cityId, datetime, isDay,
        conditionCode, iconCode, floor(temperatureC).toInt(), floor(temperatureFeelsLikeC).toInt(), uv, mayRain, maySnow)
}

internal fun List<HourWeather>.toHourWeatherModelList(): List<HourWeatherModel> = map { hourWeather -> hourWeather.toHourWeatherModel() }




