package rahulstech.weather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rahulstech.weather.database.dao.CityDao
import rahulstech.weather.database.dao.DayWeatherDao
import rahulstech.weather.database.dao.HourWeatherDao
import rahulstech.weather.database.entity.City
import rahulstech.weather.database.entity.DayWeather
import rahulstech.weather.database.entity.HourWeather
import rahulstech.weather.database.util.Converters

@Database(
    entities = [City::class, DayWeather::class, HourWeather::class],
    version = WeatherDB.DB_VERSION
)
@TypeConverters(Converters::class)
abstract class WeatherDB protected constructor(): RoomDatabase(){

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "weather.sqlite3"

        fun instance(context: Context): WeatherDB {
            return Room.databaseBuilder(context, WeatherDB::class.java, DB_NAME)
                .build()
        }
    }

    abstract fun getCityDao(): CityDao

    abstract fun getDayWeatherDao(): DayWeatherDao

    abstract fun getHourWeatherDao(): HourWeatherDao
}