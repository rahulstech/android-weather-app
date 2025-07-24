package rahulstech.weather.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import rahulstech.weather.database.entity.City

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCity(city: City): Long

    @Query("SELECT * FROM `weather_cities`")
    fun getAllCities(): List<City>

    @Query("SELECT * FROM `weather_cities` WHERE `id` = :id")
    fun getCityById(id: Long): City?

    @Query("SELECT * FROM `weather_cities` WHERE `remoteId` = :remoteId")
    fun getCityByRemoteId(remoteId: String): City?
}