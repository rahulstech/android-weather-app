package com.weather.api.model

import java.time.LocalDateTime

data class CurrentWeather(
    val last_updated: LocalDateTime,
    val temp_c: Float,
    val is_day: Boolean,
    val condition: Condition,
    val precip_mm: Float,
    val humidity: Int,
    val feelslike_c: Float,
    val uv: Float
)
