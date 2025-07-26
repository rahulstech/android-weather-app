package rahulstech.android.weatherapp.ui.forecast

import android.app.Application
import android.util.Pair
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import rahulstech.weather.repository.WeatherRepository
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.util.Resource

open class WeatherForecastViewModel(
    app: Application,
    val repository: WeatherRepository
): AndroidViewModel(app) {

    private val TAG = WeatherForecastViewModel::class.simpleName

    private val _cityForecasts
    = MutableStateFlow<Pair<Resource<CityModel>, Resource<List<DayWeatherModel>>>>(Pair(Resource.Loading(), Resource.Loading()))
    val cityForecasts: Flow<Pair<Resource<CityModel>, Resource<List<DayWeatherModel>>>> get() = _cityForecasts
    private val _cityId = MutableStateFlow<Long>(0)
    init {
        viewModelScope.launch {
            _cityId
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { id ->
                    combine(
                        repository.findCityById(id),
                        repository.getForecast(id)
                    ) { cityResource, forecastResource ->
                        Pair(cityResource, forecastResource)
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect { resources ->
                    _cityForecasts.value = resources
                }
        }
    }

    fun getForecast(id: Long) {
        _cityId.value = if (id > 0) {
            id
        }
        else {
            0
        }
    }
}