package rahulstech.weather.repository.impl

import rahulstech.weather.database.WeatherDB
import rahulstech.weather.database.dao.CityDao
import rahulstech.weather.database.dao.DayWeatherDao
import rahulstech.weather.database.dao.HourWeatherDao
import rahulstech.weather.repository.DaoProvider

internal class DaoProviderImpl(val db: WeatherDB): DaoProvider {

    override fun getCityDao(): CityDao = db.getCityDao()

    override fun getDayWeatherDao(): DayWeatherDao = db.getDayWeatherDao()

    override fun getHourWeatherDao(): HourWeatherDao = db.getHourWeatherDao()

}