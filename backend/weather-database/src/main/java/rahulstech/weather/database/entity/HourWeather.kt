package rahulstech.weather.database.entity

import androidx.room.ColumnInfo
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
    tableName = "weather_hourly",
    foreignKeys = [
        ForeignKey(entity = City::class, parentColumns = ["id"], childColumns = ["cityId"])
    ],
    indices = [
        Index(value = ["cityId", "date", "time"], name = "index_city_hour_weather", unique = true)
    ]
)
data class HourWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val cityId: Long,
    @TypeConverters(Converters::class)
    val date: LocalDate,
    @TypeConverters(Converters::class)
    val time: LocalTime,
    val isDay: Boolean,
    @TypeConverters(Converters::class)
    val lastModified: LocalDateTime,
    val conditionCode: Int,
    val iconCode: Int,
    val temperatureC: Float,
    val temperatureFeelsLikeC: Float,
    val uv: Float,
    @ColumnInfo(defaultValue = "0")
    val mayRain: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val maySnow: Boolean = false
)