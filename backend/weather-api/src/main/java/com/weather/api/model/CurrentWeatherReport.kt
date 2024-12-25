package com.weather.api.model

data class CurrentWeatherReport(
    val location: Location,
    val current: WeatherData
)
