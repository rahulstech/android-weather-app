package rahulstech.weather.repository.model

import java.time.LocalDateTime

data class HourWeatherModel(
    val id: Long,
    val cityId: Long,
    val datetime: LocalDateTime,
    val isDay: Boolean,
    val conditionCode: Int,
    val conditionIconCode: Int,
    val temperatureC: Int,
    val temperatureFeelLikeC: Int,
    val uv: Float,
    val mayRain: Boolean = false,
    val maySnow: Boolean = false
)