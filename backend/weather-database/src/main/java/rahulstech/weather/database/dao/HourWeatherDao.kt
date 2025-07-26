package rahulstech.weather.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import rahulstech.weather.database.entity.HourWeather
import java.time.LocalDate

@Dao
interface HourWeatherDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMultipleHourWeather(weathers: List<HourWeather>): List<Long>

    @Update
    fun updateHourWeather(weather: HourWeather): Int

    @Query("SELECT * FROM `weather_hourly` WHERE `cityId` = :cityId AND `date` = :date")
    fun getCityHourlyWeatherForWholeDay(cityId: Long, date: LocalDate): Flow<List<HourWeather>>
}