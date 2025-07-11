package rahulstech.weather.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import rahulstech.weather.database.util.Converters
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "weather_day",
    foreignKeys = [
        ForeignKey(entity = City::class, parentColumns = ["id"], childColumns = ["cityId"])
    ],
    indices = [
        Index(value = ["cityId", "date"], name = "index_city_day_weather")
    ]
)
data class DayWeather (
    val id: Long,
    val cityId: Long,
    @TypeConverters(Converters::class)
    val date: LocalDate,
    val conditionCode: Int,
    val iconCode: Int,
    val temperatureMaxC: Float,
    val temperatureMinC: Float,
    val humidity: Int,
    val uv: Float,
    val precipitation: Float,
    val snow: Float,
    val sunrise: LocalTime,
    val sunset: LocalTime,
)