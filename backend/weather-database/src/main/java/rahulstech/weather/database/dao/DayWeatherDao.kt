package rahulstech.weather.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import rahulstech.weather.database.entity.DayWeather
import java.time.LocalDate

@Dao
interface DayWeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDayWeather(weather: DayWeather): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMultipleDayWeather(data: List<DayWeather>): List<Long>

    @Query("SELECT * FROM `weather_day` WHERE `cityId` = :cityId AND `date` >= :start AND `date` <= :end ORDER BY `date` ASC")
    fun getCityWeatherBetweenDates(cityId: Long, start: LocalDate, end: LocalDate): List<DayWeather>

    @Query("SELECT * FROM `weather_day` WHERE `cityId` = :cityId AND `date` = :date")
    suspend fun getCityWeatherForDate(cityId: Long, date: LocalDate): DayWeather?

    @Update
    fun updateDayWeather(weather: DayWeather): Int
}