package com.weather.api.model

import java.time.LocalDate

data class ForecastDay(
    val date: LocalDate,
    val day: WeatherDay,
    val astro: Astronomy,
    val hour: List<WeatherHour>
)

data class Forecast(
    val forecastday: List<ForecastDay>
)
