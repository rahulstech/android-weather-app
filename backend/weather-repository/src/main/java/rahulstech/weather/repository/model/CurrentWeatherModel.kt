package rahulstech.weather.repository.model

import rahulstech.weather.database.entity.DayWeather
import rahulstech.weather.database.entity.HourWeather
import java.time.LocalDateTime
import kotlin.math.floor

data class CurrentWeatherModel(
    val cityId: Long,
    val datetime: LocalDateTime,
    val temperatureDayMaxC: Int,
    val temperatureDayMinC: Int,
    val temperatureDayFeelsLikeC: Int,
    val temperatureC: Int,
    val conditionCode: Int,
    val conditionIconCode: Int,
    val uv: Float,
    val mayRain: Boolean = false,
    val maySnow: Boolean = false,
    val lastModified: LocalDateTime
) {

    companion object {
        internal fun transform(localDayWeather: DayWeather, localHourWeather: HourWeather, datetime: LocalDateTime): CurrentWeatherModel {
            return CurrentWeatherModel(
                localDayWeather.cityId,
                datetime,
                floor(localDayWeather.temperatureMaxC).toInt(), floor(localDayWeather.temperatureMinC).toInt(),
                floor(localHourWeather.temperatureC).toInt(), floor(localHourWeather.temperatureFeelsLikeC).toInt(),
                localHourWeather.conditionCode, localHourWeather.iconCode,
                localHourWeather.uv, localHourWeather.mayRain, localHourWeather.maySnow,
                localHourWeather.lastModified
            )
        }
    }
}
