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

}