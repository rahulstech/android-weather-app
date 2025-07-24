package rahulstech.weather.repository

import rahulstech.weather.database.dao.CityDao
import rahulstech.weather.database.dao.DayWeatherDao
import rahulstech.weather.database.dao.HourWeatherDao

interface DaoProvider {

    fun getCityDao(): CityDao

    fun getDayWeatherDao(): DayWeatherDao

    fun getHourWeatherDao(): HourWeatherDao
}