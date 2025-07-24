package com.weather.api.model

import java.time.LocalDateTime

data class WeatherHour(
    val time: LocalDateTime,
    val temp_c: Float,
    val is_day: Boolean,
    val condition: Condition,
    val feelslike_c: Float,
    val will_it_rain: Boolean,
    val will_it_snow: Boolean,
    val uv: Float
)