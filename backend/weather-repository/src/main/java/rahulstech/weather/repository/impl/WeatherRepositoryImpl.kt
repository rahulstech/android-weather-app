package rahulstech.weather.repository.impl

import android.util.Log
import com.weather.api.WeatherApi
import com.weather.api.model.Forecast
import com.weather.api.model.ForecastDay
import com.weather.api.model.Location
import com.weather.api.util.parseErrorBody
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import rahulstech.weather.repository.BuildConfig
import rahulstech.weather.repository.DaoProvider
import rahulstech.weather.repository.WeatherRepository
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.CurrentWeatherModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import rahulstech.weather.repository.util.Resource
import rahulstech.weather.repository.util.WeatherRepositoryException
import rahulstech.weather.repository.util.toCitiesList
import rahulstech.weather.repository.util.toCity
import rahulstech.weather.repository.util.toCityModel
import rahulstech.weather.repository.util.toCityModelList
import rahulstech.weather.repository.util.toDayWeather
import rahulstech.weather.repository.util.toDayWeatherList
import rahulstech.weather.repository.util.toDayWeatherModel
import rahulstech.weather.repository.util.toDayWeatherModelList
import rahulstech.weather.repository.util.toHourWeatherList
import rahulstech.weather.repository.util.toHourWeatherModelList
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

internal class WeatherRepositoryImpl(
    val daos: DaoProvider,
    val api: WeatherApi,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
): WeatherRepository {

    companion object {
        private val TAG = WeatherRepositoryImpl::class.simpleName
        private const val FORECAST_DAYS = 3
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

    override suspend fun getHourlyForecast(
        cityId: Long,
        date: LocalDate
    ): Flow<Resource<List<HourWeatherModel>>> = flow {
        emit(Resource.Loading())

        daos.getHourWeatherDao().getCityHourlyWeatherForWholeDay(cityId, date)
            .map { result ->
                result.toHourWeatherModelList()
            }
            .catch { ex ->
                emit(Resource.Error(WeatherRepositoryException("can not load local hourly forecast", ex)))
            }
            .collect { emit(Resource.Success(it)) }
    }

    override suspend fun getForecastForDate(
        cityId: Long,
        date: LocalDate
    ): Flow<Resource<DayWeatherModel>> = flow {
        emit(Resource.Loading())
        try {
            var dayWeather = getLocalWeatherForecastForDate(cityId, date)
            if (null == dayWeather) {
                Log.i(TAG, "dayWeather for city=$cityId and date=$date not found locally; will fetch from api")
                // weather for the given date not available, so fetch weather for given date
                fetchWeatherForecastForDate(cityId, date)
                dayWeather = getLocalWeatherForecastForDate(cityId, date)
            }
            emit(Resource.Success(dayWeather))
        }
        catch (ex: WeatherRepositoryException) {
            emit(Resource.Error(ex))
        }
        catch (ex: Exception) {
            emit(Resource.Error(WeatherRepositoryException("",ex)))
        }
    }

    private suspend fun getLocalWeatherForecastForDate(cityId: Long, date: LocalDate): DayWeatherModel? {
        val dayDao = daos.getDayWeatherDao()
        try {
            val dayWeather = dayDao.getCityWeatherForDate(cityId, date)
            return dayWeather?.toDayWeatherModel()
        }
        catch (ex: Exception) {
            throw WeatherRepositoryException("unable to load local city weather for date",ex)
        }
    }

    private suspend fun fetchWeatherForecastForDate(cityId: Long, date: LocalDate) {
        val remoteCityId = getCityRemoteId(cityId)
        val remoteData = getRemoteWeatherForecastForDate(remoteCityId, date)
        Log.d(TAG, "fetched remote day forecast ${remoteData.day}")

        val localDayData = remoteData.toDayWeather(cityId)
        Log.d(TAG,"remote day forecast to local day forecast $localDayData")
        daos.getDayWeatherDao().addDayWeather(localDayData)

        val localHourData = withContext(dispatcher) {  remoteData.hour.toHourWeatherList(cityId) }
        Log.i(TAG, "size of loaded hourly forecast ${localHourData.size}")
        daos.getHourWeatherDao().addMultipleHourWeather(localHourData)
    }

    private suspend fun getRemoteWeatherForecastForDate(cityId: String, date: LocalDate): ForecastDay {
        val response = api.getForecastForDate("id:$cityId", date.format(DateTimeFormatter.ISO_DATE))
        if (response.isSuccessful) {
            return response.body()?.forecast?.forecastday?.get(0)!!
        }
        else {
            val errorResponse = response.parseErrorBody()
            throw WeatherRepositoryException("unable to fetch remote weather forecast; error=$errorResponse")
        }
    }

    override suspend fun getForecast(cityId: Long): Flow<Resource<List<DayWeatherModel>>> = flow {
        emit(Resource.Loading())
        try {
            var weatherDays = getLocalWeatherForecast(cityId, FORECAST_DAYS)
            if (weatherDays.size < FORECAST_DAYS) {
                // required number of forecast days are not available locally, so fetch from api
                fetchWeatherForecastForDays(cityId, FORECAST_DAYS)
                weatherDays = getLocalWeatherForecast(cityId, FORECAST_DAYS)
            }
            emit(Resource.Success(weatherDays))
        }
        catch (ex: WeatherRepositoryException) {
            emit(Resource.Error(ex))
        }
        catch (ex: Exception) {
            emit(Resource.Error(WeatherRepositoryException("")))
        }
    }

    private suspend fun fetchWeatherForecastForDays(cityId: Long, days: Int) {
        val cityRemoteId = getCityRemoteId(cityId)
        val forecast = getRemoteWeatherForecastForDays(cityRemoteId, days)
        val forecastDays = forecast.forecastday
        if (forecastDays.isEmpty()) {
            return
        }

        // insert day weather
        val weatherDays = withContext(dispatcher) { forecastDays.toDayWeatherList(cityId) }
        daos.getDayWeatherDao().addMultipleDayWeather(weatherDays)

        // insert hour weather
        val weatherHours = withContext(dispatcher) { forecastDays.toHourWeatherList(cityId) }
        daos.getHourWeatherDao().addMultipleHourWeather(weatherHours)
    }

    private suspend fun getLocalWeatherForecast(cityId: Long, days: Int): List<DayWeatherModel> {
        try {
            val startDate = LocalDate.now()
            val endDate = startDate.plusDays(days.toLong())
            val weatherDay = daos.getDayWeatherDao().getCityWeatherBetweenDates(cityId, startDate, endDate)
            val models = withContext(dispatcher) { weatherDay.toDayWeatherModelList() }
            return models
        }
        catch (ex: Exception) {
            throw WeatherRepositoryException("unable to load local weather forecast", ex)
        }
    }

    private suspend fun getRemoteWeatherForecastForDays(cityId: String, days: Int): Forecast {
        val response = api.getForecast("id:$cityId", days)
        if (response.isSuccessful) {
            return response.body()?.forecast!!
        } else {
            val errorResponse = response.parseErrorBody()
            throw WeatherRepositoryException("unable to fetch remote weather forecast; error=$errorResponse")
        }
    }

    private suspend fun getCityRemoteId(cityId: Long): String {
        return getLocalCityById(cityId)?.remoteId ?: ""
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