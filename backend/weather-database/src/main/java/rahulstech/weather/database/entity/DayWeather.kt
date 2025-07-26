package rahulstech.weather.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import rahulstech.weather.database.util.Converters
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(
    tableName = "weather_day",
    foreignKeys = [
        ForeignKey(entity = City::class, parentColumns = ["id"], childColumns = ["cityId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [
        Index(value = ["cityId", "date"], name = "index_city_day_weather", unique = true)
    ]
)
data class DayWeather (
    @PrimaryKey(autoGenerate = true)
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
    val precipitationMm: Float,
    val snowCm: Float,
    @TypeConverters(Converters::class)
    val sunrise: LocalTime,
    @TypeConverters(Converters::class)
    val sunset: LocalTime,
    @TypeConverters(Converters::class)
    val lastModified: LocalDateTime = LocalDateTime.now()
)