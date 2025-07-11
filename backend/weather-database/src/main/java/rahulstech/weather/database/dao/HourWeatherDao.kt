package rahulstech.weather.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import rahulstech.weather.database.entity.HourWeather
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface HourWeatherDao {
    @Insert
    fun addHourWeather(weather: HourWeather): Long

    @Update
    fun updateHourWeather(weather: HourWeather): Int

    @Query("SELECT * FROM `weather_hourly` WHERE `cityId` = :cityId AND `date` = :date AND `time` = :time")
    fun getCityHourlyWeatherForHour(cityId: Long, date: LocalDate, time: LocalTime): LiveData<HourWeather>

    @Query("SELECT * FROM `weather_hourly` WHERE `cityId` = :cityId AND `date` = :date")
    fun getCityHourlyWeatherForWholeDay(cityId: Long, date: LocalDate): LiveData<List<HourWeather>>
}