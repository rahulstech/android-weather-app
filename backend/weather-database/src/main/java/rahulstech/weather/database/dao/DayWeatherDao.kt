package rahulstech.weather.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import rahulstech.weather.database.entity.DayWeather
import java.time.LocalDate

@Dao
interface DayWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDayWeather(weather: DayWeather): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMultipleDayWeather(data: List<DayWeather>): List<Long>

    @Query("SELECT * FROM `weather_day` WHERE `cityId` = :cityId AND `date` >= :start AND `date` <= :end ORDER BY `date` ASC")
    fun getCityWeatherBetweenDates(cityId: Long, start: LocalDate, end: LocalDate): Flow<List<DayWeather>>

    @Query("SELECT * FROM `weather_day` WHERE `cityId` = :cityId AND `date` = :date")
    fun getCityWeatherForDate(cityId: Long, date: LocalDate): Flow<DayWeather?>
}