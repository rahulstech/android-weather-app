package rahulstech.weather.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_cities",
    indices = [
        Index(value = ["remoteId"], name = "unique_remote_city_id", unique = true)
    ]
)
data class City(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val country: String,
    val longitude: Float,
    val latitude: Float,
    val remoteId: String
)