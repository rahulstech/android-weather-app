package rahulstech.weather.database.util

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Converters {

    companion object {
        private val FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        private val FORMAT_TIME = DateTimeFormatter.ofPattern("HH:mm")
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.format(DateTimeFormatter.ISO_DATE)

    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? = if (date.isNullOrBlank()) {
        null
    }else {
        LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.format(FORMAT_TIME)

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? = if(time.isNullOrBlank()) {
        null
    }else {
        LocalTime.parse(time, FORMAT_TIME)
    }

    @TypeConverter
    fun fromLocalDateTime(datetime: String?): LocalDateTime? = if(datetime.isNullOrBlank()) {
        null
    }else {
        LocalDateTime.parse(datetime, FORMAT_DATETIME)
    }

    @TypeConverter
    fun toLocalDateTime(datetime: LocalDateTime?): String? = datetime?.format(FORMAT_DATETIME)
}