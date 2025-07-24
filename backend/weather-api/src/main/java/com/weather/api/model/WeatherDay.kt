package com.weather.api.model

data class WeatherDay(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val totalprecip_mm: Float,
    val totalsnow_cm: Float,
    val avghumidity: Int,
    val condition: Condition,
    val uv: Float
)
