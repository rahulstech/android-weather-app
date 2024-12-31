package rahulstech.weather.repository

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class City(
    val id: Long, val locationId: String,
    val name: String, val region: String, val country: String,
    val lat: Double, val lon: Double,
    val timezone: ZoneId?
)

data class HourlyWeather(val city: City, val datetime: LocalDateTime, val isDay: Boolean,
                         val condition: WeatherCondition, val temp: Float, val feelsLike: Float,
                         val precipitation: Float, val humidity: Float, val uv: Float)

data class DailyWeather(val city: City,
                        val date: LocalDate, val condition: WeatherCondition,
                        val maxTemp: Float, val minTemp: Float, val avgTemp: Float,
                        val totalPrecipitation: Float, val avgHumidity: Float, val uv: Float,
                        val sunrise: LocalTime, val sunset: LocalTime)

data class WeatherForecast(val city: City, val day: DailyWeather, val hours: List<HourlyWeather>)

enum class WeatherCondition {

    Fine,

    Fog,

    Mist,

    Cloudy,

    Rainy,

    Thunder,

    Snow,

    Sleet,

    Blizzard,

    ;

    companion object {
        fun get(code: String): WeatherCondition {
            return when (code) {
                "1000" -> Fine

                "1003", "1006", "1009" -> Cloudy

                "1030" -> Mist

                "1135", "1147" -> Fog

                "1087", "1273",
                "1276", "1279", "1282" -> Thunder

                "1072", "1063", "1183",
                "1180", "1186", "1189",
                "1192", "1195", "1198",
                "1201", "1240", "1243",
                "1246", "1150", "1153",
                "1168", "1171" -> Rainy

                "1255", "1258", "1261",
                "1264", "1237", "1225",
                "1222", "1219", "1216",
                "1114", "1066", "1210" -> Snow

                "1069", "1024", "1207" -> Sleet

                "1117" -> Blizzard

                else -> throw IllegalArgumentException("unknown condition $code")
            }
        }
    }
}


