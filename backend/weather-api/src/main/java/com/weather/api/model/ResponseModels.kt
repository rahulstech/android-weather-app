package com.weather.api.model

data class CurrentWeatherResponse(
    val current: CurrentWeather
)

data class ErrorResponse(
    val code: Int,
    val message: String
)

data class ForecastResponse(
    val forecast: Forecast
)