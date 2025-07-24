package rahulstech.android.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import rahulstech.weather.repository.WeatherRepository
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import rahulstech.weather.repository.util.Resource
import java.time.LocalDate

class HomeViewModel(
    app: Application,
    private val repository: WeatherRepository
) : AndroidViewModel(app) {

    private val _city = MutableStateFlow<Resource<CityModel>>(Resource.Loading())
    val city: Flow<Resource<CityModel>> get() = _city
    private val _dailyForecast = MutableStateFlow<Resource<DayWeatherModel>>(Resource.Loading())
    val dailyForecast: Flow<Resource<DayWeatherModel>> get() = _dailyForecast
    private val _hourlyForecast = MutableStateFlow<Resource<List<HourWeatherModel>>>(Resource.Loading())
    val hourlyForecast: Flow<Resource<List<HourWeatherModel>>> get() = _hourlyForecast
    private val _cityId = MutableStateFlow<Long?>(null)
    init {
        viewModelScope.launch {
            _cityId
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { id ->
                    repository.getForecastForDate(id, LocalDate.now())
                }
                .flowOn(Dispatchers.IO)
                .collect {
                    _dailyForecast.value = it
                }
        }

        viewModelScope.launch {
            _cityId
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { id ->
                    repository.getHourlyForecast(id, LocalDate.now())
                }
                .flowOn(Dispatchers.IO)
                .collect {
                    _hourlyForecast.value = it
                }
        }

        viewModelScope.launch {
            _cityId
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { id ->
                    repository.findCityById(id)
                }
                .flowOn(Dispatchers.IO)
                .collect {
                    _city.value = it
                }
        }
    }

    fun changeCurrentCity(cityId: Long) {
        _cityId.value = if (cityId > 0 ) {
            cityId
        }
        else {
            null
        }
    }
}
