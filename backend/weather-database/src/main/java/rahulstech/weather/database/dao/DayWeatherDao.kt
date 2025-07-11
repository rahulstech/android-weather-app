package rahulstech.weather.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import rahulstech.weather.database.entity.DayWeather
import java.time.LocalDate

@Dao
interface DayWeatherDao {

    @Insert
    fun addDayWeather(weather: DayWeather): Long

    @Query("SELECT * FROM `weather_day` WHERE `cityId` = :cityId AND `date` >= :start AND `date` <= :end ORDER BY `date` ASC")
    fun getCityWeatherBetweenDates(cityId: Long, start: LocalDate, end: LocalDate): List<DayWeather>
}