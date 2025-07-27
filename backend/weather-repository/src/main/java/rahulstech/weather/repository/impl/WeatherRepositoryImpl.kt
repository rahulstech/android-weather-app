package rahulstech.weather.repository.impl

import android.util.Log
import com.weather.api.WeatherApi
import com.weather.api.model.ForecastDay
import com.weather.api.model.Location
import com.weather.api.util.parseErrorBody
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import rahulstech.weather.database.entity.DayWeather
import rahulstech.weather.database.entity.HourWeather
import rahulstech.weather.repository.DaoProvider
import rahulstech.weather.repository.WeatherRepository
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import rahulstech.weather.repository.util.Resource
import rahulstech.weather.repository.util.WeatherRepositoryException
import rahulstech.weather.repository.util.toCity
import rahulstech.weather.repository.util.toCityModel
import rahulstech.weather.repository.util.toCityModelList
import rahulstech.weather.repository.util.toDayWeather
import rahulstech.weather.repository.util.toDayWeatherModel
import rahulstech.weather.repository.util.toDayWeatherModelList
import rahulstech.weather.repository.util.toHourWeatherList
import rahulstech.weather.repository.util.toHourWeatherModelList
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class WeatherRepositoryImpl(
    val daos: DaoProvider,
    val api: WeatherApi,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
): WeatherRepository {

    companion object {
        private val TAG = WeatherRepositoryImpl::class.simpleName
        private const val FORECAST_DAYS = 7
        private const val HOURLY_FORECAST_REFRESH_COUNT = 6
        private const val HOURLY_FORECAST_REFRESH_RATE = (24 / HOURLY_FORECAST_REFRESH_COUNT) * 60 // in minutes
    }

//    private var _citySearchJob: Job? = null
//
//    override suspend fun searchCity(keyword: String?): Flow<Resource<List<CityModel>>> = flow<Resource<List<CityModel>>> {
//
//        Log.i(TAG, "searchCity: keyword=$keyword")
//
//        // cancel if there is any running search
//        if (_citySearchJob?.isActive == true) {
//            _citySearchJob?.cancel()
//        }
//
//        emit(Resource.Loading())
//
//        try {
//            val remoteCities = if (keyword.isNullOrBlank()) {
//                emptyList()
//            }
//            else {
//                searchRemoteCities(keyword)
//            }
//
//            if (_citySearchJob?.isActive == true) {
//                val cities = withContext(dispatcher) { remoteCities.toCityModelList() }
//                emit(Resource.Success(cities))
//            }
//
//            val cities = withContext(dispatcher) { remoteCities.toCityModelList() }
//            emit(Resource.Success(cities))
//
//        }
//        catch (ignore: CancellationException) {}
//        catch (ex: Exception) { emit(Resource.Error(ex)) }
//        finally {
//            if (_citySearchJob?.isActive == false) {
//                _citySearchJob = null
//            }
//        }
//    }

    ////////////////////////////////////////////////////////////
    //                       City                            //
    //////////////////////////////////////////////////////////

    override suspend fun searchCity(keyword: String?): Flow<Resource<List<CityModel>>> = flow<Resource<List<CityModel>>> {
        Log.d(TAG, "searchCity: keyword=$keyword")

        emit(Resource.Loading())
        try {
            val remoteCities = if (keyword.isNullOrBlank()) {
                emptyList()
            }
            else {
                searchRemoteCities(keyword)
            }
            val cities = withContext(dispatcher) { remoteCities.toCityModelList() }
            emit(Resource.Success(cities))
        }
        catch (ex: Exception) { emit(Resource.Error(ex)) }
    }

    suspend fun searchRemoteCities(keyword: String): List<Location> {
        val response = api.searchCity(keyword)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        else {
            val err = response.parseErrorBody()
            throw WeatherRepositoryException("city search api call failed with error=$err")
        }
    }

    override suspend fun saveCity(cityModel: CityModel): Flow<Resource<CityModel>> = flow {
        emit(Resource.Loading())
        try {
            val dao = daos.getCityDao()
            val city = cityModel.toCity()

            // insert the city; if city with remoteId already saved the returned id will be -1
            dao.addCity(city)

            // now query for city with remoteId, query result may be null
            val result = dao.getCityByRemoteId(city.remoteId)

            if (null == result) {
                emit(Resource.Error(WeatherRepositoryException("city not saved")))
            }
            else {
                emit(Resource.Success(result.toCityModel()))
            }
        }
        catch (ex: Exception) {
            Log.e(TAG, "city not saved", ex)
            emit(Resource.Error(WeatherRepositoryException("city not save")))
        }
    }

    override suspend fun findCityById(cityId: Long): Flow<Resource<CityModel>> = flow {
        emit(Resource.Loading())

        try {
            val city = getLocalCityById(cityId)
            if (city == null) {
                emit(Resource.Error(WeatherRepositoryException("no city found for id=$cityId")))
            }
            else {
                emit(Resource.Success(city))
            }
        }
        catch (ex: Exception) {
            emit(Resource.Error(WeatherRepositoryException("findCityById db error", ex)))
        }
    }

    override suspend fun getAllSavedCities(): Flow<Resource<List<CityModel>>> = flow {
        emit(Resource.Loading())

        daos.getCityDao()
            .getAllCities()
            .catch { ex ->
                emit(Resource.Error(WeatherRepositoryException("unable to fetch local cities", ex)))
            }
            .map { cities -> cities.toCityModelList() }
            .collect { cities ->
                emit(Resource.Success(cities))
            }
    }

    override suspend fun removeCity(cityId: Long): Flow<Resource<CityModel>> = flow {
        emit(Resource.Loading())

        try {
            val city = getLocalCityById(cityId);
            if (null == city) {
                emit(Resource.Error(WeatherRepositoryException("no city found for id $cityId")))
            }
            else {
                daos.getCityDao().deleteCity(cityId)
                emit(Resource.Success(city))
            }
        }
        catch (ex: WeatherRepositoryException) {
            emit(Resource.Error(ex))
        }
        catch (ex: Exception) {
            emit(Resource.Error(WeatherRepositoryException("can not remove city locally", ex)))
        }
    }

    ////////////////////////////////////////////////////////////
    //                      Forecast                         //
    //////////////////////////////////////////////////////////

    override suspend fun getForecastForDate(
        cityId: Long,
        date: LocalDate
    ): Flow<Resource<DayWeatherModel>> = channelFlow {
        send(Resource.Loading())
        val dayWeatherFlow = daos.getDayWeatherDao().getCityWeatherForDate(cityId, date)
        dayWeatherFlow.collect { dayWeather ->
            if (null == dayWeather || dayWeather.lastModified.isBefore(LocalDate.now())) {
                try {
                    // either no forecast found or it is not updated, fetch the latest data from api and save locally
                    Log.i(TAG, "dayWeather for city $cityId and date $date not found locally or old data found; will fetch from api")
                    val pair = fetchWeatherForecastForDate(cityId, date)
                    saveWeatherForecast(pair)
                }
                catch (ex: WeatherRepositoryException) {
                    send(Resource.Error(ex))
                }
                catch (ex: Exception) {
                    Log.e(TAG,"unable to get forecast for date", ex)
                    send(Resource.Error(WeatherRepositoryException("unable to get forecast for date")))
                }
            }
            else {
                send(Resource.Success(dayWeather.toDayWeatherModel()))
            }
        }
    }

    override suspend fun getForecast(cityId: Long): Flow<Resource<List<DayWeatherModel>>> = channelFlow {
        send(Resource.Loading())

        val dayWeathersFlow = getLocalWeatherForecast(cityId, FORECAST_DAYS)
        dayWeathersFlow.collect { forecasts ->
            try {
                // if no forecast available then fetch forecasts for mentioned days
                if (forecasts.isEmpty()) {
                    Log.i(TAG, "no forecast available locally")
                    val pairs = fetchWeatherForecastForDays(cityId, FORECAST_DAYS)
                    saveMultipleWeatherForecasts(pairs)
                    return@collect
                }

                // if available either partial or all but data is outdated the refresh it
                val today = LocalDate.now()
                var dates = forecasts.filter { forecast ->
                    // filter forecasts that lastModified before today
                    forecast.lastModified.isBefore(today)
                }
                    .map { forecast ->
                        // return the forecast date only
                        forecast.date
                    }

                // if all local forecast data is up to date and requested numbers of days forecasts available, then send it
                if (forecasts.size == FORECAST_DAYS && dates.isEmpty()) {
                    Log.i(TAG,"all requested forecasts available locally and up to date")
                    val dayWeathers = withContext(dispatcher) {
                        forecasts.toDayWeatherModelList()
                    }
                    send(Resource.Success(dayWeathers))
                    return@collect
                }

                // if less number of forecasts locally available then download those also
                if (forecasts.size < FORECAST_DAYS) {
                    val dateUntil = today.plusDays(FORECAST_DAYS.toLong())
                    val remainingDates = mutableListOf<LocalDate>()
                    var date = if (dates.isEmpty()) {
                        today.plusDays(1)
                    }
                    else {
                        dates.last().plusDays(1)
                    }
                    while (date.isBefore(dateUntil)) {
                        remainingDates.add(date)
                        date = date.plusDays(1)
                    }
                    dates = dates.toMutableList().apply {
                        addAll(remainingDates)
                    }
                }

                // now fetch forecast for the dates only from api and save locally
                Log.i(TAG, "fetch forecast for dates unavailable or outdated")
                val pairs = fetchWeatherForecastForDates(cityId, dates)
                saveMultipleWeatherForecasts(pairs)
            }
            catch (ex: Exception) {
                Log.e(TAG, "getForecast error", ex)
                val cause = when(ex) {
                    is WeatherRepositoryException -> ex
                    else -> WeatherRepositoryException("internal error")
                }
                send(Resource.Error(cause))
            }
        }
    }

    override suspend fun getHourlyForecast(
        cityId: Long,
        date: LocalDate
    ): Flow<Resource<List<HourWeatherModel>>> = channelFlow {
        send(Resource.Loading())

        daos.getHourWeatherDao().getCityHourlyWeatherForWholeDay(cityId, date)
            .collect { forecasts ->
                if (forecasts.isEmpty()) {
                    return@collect
                }

                val hour = LocalTime.now().hour
                val hourForecast = forecasts[hour]
                // expected last modified is the HOURLY_FORECAST_REFRESH_RATE minutes back from now
                val expectedLastModified = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour,0)).minusMinutes(HOURLY_FORECAST_REFRESH_RATE.toLong())
                if (!expectedLastModified.isBefore(hourForecast.lastModified)) {
                    // hourly forecast of current hour was updated HOURLY_FORECAST_REFRESH_RATE or more minutes,
                    // so  need to refresh the hourly forecast
                    try {
                        val pair = fetchWeatherForecastForDate(cityId, date)
                        saveWeatherForecast(pair)
                    }
                    catch (ex: Exception) {
                        Log.e(TAG,"fail to save refreshed hourly forecast", ex)
                        val cause = when(ex) {
                            !is WeatherRepositoryException -> WeatherRepositoryException("internal error")
                            else -> ex
                        }
                        send(Resource.Error(cause))
                    }
                }
                else {
                    // hourly forecast is up to date, send it
                    val hourlyWeather = withContext(dispatcher) { forecasts.toHourWeatherModelList() }
                    send(Resource.Success(hourlyWeather))
                }
            }
    }

    ////////////////////////////////////////////////////////////
    //                      Helpers                          //
    //////////////////////////////////////////////////////////

    private suspend fun fetchWeatherForecastForDate(cityId: Long, date: LocalDate): Pair<DayWeather, List<HourWeather>> {
        return fetchWeatherForecastForDates(cityId, listOf(date))[0]
    }

    private fun getLocalWeatherForecast(cityId: Long, days: Int): Flow<List<DayWeather>> {
        try {
            val startDate = LocalDate.now()
            val endDate = startDate.plusDays(days.toLong()-1)
            return daos.getDayWeatherDao().getCityWeatherBetweenDates(cityId, startDate, endDate)
        }
        catch (ex: Exception) {
            Log.e(TAG, "loading weather forecast for city $cityId and days $days locally failed", ex)
            throw WeatherRepositoryException("loading weather forecast locally failed")
        }
    }

    private suspend fun fetchWeatherForecastForDays(cityId: Long, days: Int = 1): List<Pair<DayWeather, List<HourWeather>>> {
//        val remoteCityId = getCityRemoteId(cityId)
//        Log.i(TAG, "remote city id for city $cityId = $remoteCityId")

        val dates = (0 until days).map { day ->
            LocalDate.now().plusDays(day.toLong())
        }

        return fetchWeatherForecastForDates(cityId, dates)


//
//
//
//
//        val forecast = getRemoteWeatherForecastForDays(remoteCityId, days)
//        val forecastDays = forecast.forecastday
//        if (forecastDays.isEmpty()) {
//            return emptyList()
//        }
//
//        // insert day weather
//        val weatherDays = withContext(dispatcher) { forecastDays.toDayWeatherList(cityId) }
//        daos.getDayWeatherDao().addMultipleDayWeather(weatherDays)
//
//        try {
//            val response = api.getForecast("id:$remoteCityId", days)
//            if (response.isSuccessful) {
//                val forecastDays = response.body()?.forecast?.forecastday
//                if (!forecastDays.isNullOrEmpty()) {
//                    return withContext(dispatcher) {
//                        forecastDays.map { forecastDay ->
//                            val dayWeather = forecastDay.toDayWeather(cityId)
//                            val hourWeatherList = forecastDay.hour.toHourWeatherList(cityId)
//                            dayWeather to hourWeatherList
//                        }
//                    }
//                }
//            } else {
//                val errorResponse = response.parseErrorBody()
//                Log.e(TAG, "api error occurred fetching weather forecast; code=${errorResponse?.code} message=${errorResponse?.message}")
//                throw WeatherRepositoryException("api error")
//            }
//        }
//        catch (ex: Exception) {
//            Log.e(TAG, "error occurred fetching weather forecast", ex)
//            throw WeatherRepositoryException("internal error")
//        }
//
//        // insert hour weather
//        val weatherHours = withContext(dispatcher) { forecastDays.toHourWeatherList(cityId) }
//        daos.getHourWeatherDao().addMultipleHourWeather(weatherHours)
    }

    private suspend fun fetchWeatherForecastForDates(cityId: Long, dates: List<LocalDate>): List<Pair<DayWeather, List<HourWeather>>> {
        val remoteCityId = getCityRemoteId(cityId)
        Log.i(TAG, "remote city id for city $cityId = $remoteCityId")

        val flows = dates.map { date ->
            fetchWeatherForecastForDate(remoteCityId, date)
        }
        return combine(
            flows
        ) { forecastDays ->
            Log.i(TAG, "weather forecast fetched for given dates")
            withContext(dispatcher) {
                forecastDays.map { forecastDay ->
                    val dayWeather = forecastDay.toDayWeather(cityId)
                    val hourWeatherList = forecastDay.hour.toHourWeatherList(cityId)
                    dayWeather to hourWeatherList
                }
            }
        }
            .catch { ex ->
                throw ex
            }
            .first()
    }

    private fun fetchWeatherForecastForDate(remoteCityId: String, date: LocalDate): Flow<ForecastDay> {
        Log.i(TAG,"fetch weather forecast for city $remoteCityId and date $date")
        return flow {
            val response = api.getForecastForDate("id:$remoteCityId", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            if (response.isSuccessful) {
                val forecastDay = response.body()?.forecast?.forecastday?.getOrNull(0)
                forecastDay?.let { emit(forecastDay) } ?: throw WeatherRepositoryException("empty api response")

            } else {
                val errorResponse = response.parseErrorBody()
                Log.e(TAG, "api error occurred fetching weather forecast for cityId=$remoteCityId and date=$date; code=${errorResponse?.code} message=${errorResponse?.message}")
                throw WeatherRepositoryException("api error")
            }
        }
            .catch { ex ->
                Log.e(TAG, "error occurred fetching weather forecast cityId=$remoteCityId and date=$date", ex)
                throw when(ex) {
                    is WeatherRepositoryException -> ex
                    else -> WeatherRepositoryException("internal error")
                }
            }
    }

    private suspend fun saveWeatherForecast(forecast: Pair<DayWeather, List<HourWeather>>) {
        saveMultipleWeatherForecasts(listOf(forecast))
    }

    private suspend fun saveMultipleWeatherForecasts(forecasts: List<Pair<DayWeather, List<HourWeather>>>) {
        val dayWeathers = forecasts.map { pair -> pair.first }
        val hourWeatherList = forecasts.flatMap { pair -> pair.second }

        try {
            daos.getDayWeatherDao().addMultipleDayWeather(dayWeathers)
            daos.getHourWeatherDao().addMultipleHourWeather(hourWeatherList)
        }
        catch (ex: Exception) {
            Log.e(TAG,"unable to add weather forecast", ex)
            throw WeatherRepositoryException("unable to add weather forecast")
        }
    }

    private suspend fun getCityRemoteId(cityId: Long): String {
        return getLocalCityById(cityId)?.remoteId ?: ""
    }

    private suspend fun getLocalCityById(id: Long): CityModel? {
        val cityDao = daos.getCityDao()
        try {
            val city = cityDao.getCityById(id)
            return city?.toCityModel()
        }
        catch (ex: Exception) {
            throw WeatherRepositoryException("unable to get local city by id", ex)
        }
    }
}