package rahulstech.weather.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import rahulstech.weather.database.entity.HourWeather
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface HourWeatherDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMultipleHourWeather(weathers: List<HourWeather>): List<Long>

    @Update
    fun updateHourWeather(weather: HourWeather): Int

    @Query("SELECT * FROM `weather_hourly` WHERE `cityId` = :cityId AND `date` = :date AND `time` = :time")
    fun getCityHourlyWeatherForHour(cityId: Long, date: LocalDate, time: LocalTime): Flow<HourWeather?>

    @Query("SELECT * FROM `weather_hourly` WHERE `cityId` = :cityId AND `date` = :date")
    fun getCityHourlyWeatherForWholeDay(cityId: Long, date: LocalDate): List<HourWeather>
}