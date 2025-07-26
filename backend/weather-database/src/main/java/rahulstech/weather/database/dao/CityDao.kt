package rahulstech.weather.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import rahulstech.weather.database.entity.City

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCity(city: City): Long

    @Query("SELECT * FROM `weather_cities`")
    fun getAllCities(): Flow<List<City>>

    @Query("SELECT * FROM `weather_cities` WHERE `id` = :id")
    suspend fun getCityById(id: Long): City?

    @Query("SELECT * FROM `weather_cities` WHERE `remoteId` = :remoteId")
    fun getCityByRemoteId(remoteId: String): City?

    @Query("DELETE FROM `weather_cities` WHERE `id` = :id")
    suspend fun deleteCity(id: Long): Int
}