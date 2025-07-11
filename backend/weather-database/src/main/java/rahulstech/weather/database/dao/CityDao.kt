package rahulstech.weather.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import rahulstech.weather.database.entity.City

@Dao
interface CityDao {

    @Insert
    fun addCity(city: City): Long

    @Query("SELECT * FROM `weather_cities`")
    fun getAllCities(): List<City>
}