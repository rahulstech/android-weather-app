package rahulstech.weather.repository.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class DayWeatherModel(
    val id: Long,
    val cityId: Long,
    val date: LocalDate,
    val conditionCode: Int,
    val conditionIconCode: Int,
    val temperatureMaxC: Int,
    val temperatureMinC: Int,
    val humidity: Int,
    val uv: Float,
    val precipitation: Float,
    val snow: Float,
    val sunrise: LocalTime,
    val sunset: LocalTime,
    val lastModified: LocalDateTime
)
