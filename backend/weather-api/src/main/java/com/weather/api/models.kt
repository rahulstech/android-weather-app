package com.weather.api

data class Location(
    val id: String,
    val name: String, val region: String, val country: String,
    val lat: Double, val lon: Double,
    val tz_id: String, val localtime: String
)

data class Timezone(val location: Location)

data class Condition(val text: String, val code: String)

data class WeatherData(val condition: Condition, val time: String, val is_day: Int,
                       val temp_c: Float, val feelslike_c: Float,
                       val precip_mm: Float, val humidity: Float, val uv: Float)

data class Astronomy(val sunrise: String, val sunset: String)

data class DayWeatherData( val condition: Condition,
                            val maxtemp_c: Float, val mintemp_c: Float, val avgtmp_c: Float,
                            val totalprecip_mm: Float, val avghumidity: Float, val  uv: Float)

data class ForecastDay(val date: String, val day: DayWeatherData, val astro: Astronomy, val hour: List<WeatherData>)

data class ForecastDays(val forecastday: List<ForecastDay>)

data class Forecast(val location: Location, val forecast: ForecastDays)

