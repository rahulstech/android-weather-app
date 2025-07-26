package rahulstech.android.weatherapp.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import rahulstech.weather.repository.WeatherRepository
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.util.Resource

class CitySearchViewModel(
    app: Application,
    val repository: WeatherRepository
): AndroidViewModel(app) {

    private val TAG = CitySearchViewModel::class.simpleName

    private var _savedCities: Flow<Resource<List<CityModel>>>? = null
    suspend fun getAllCities(): Flow<Resource<List<CityModel>>> {
        if (null == _savedCities) {
            _savedCities = repository.getAllSavedCities()
        }
        return _savedCities!!
    }

    private val _removeCityResult = MutableStateFlow<Resource<CityModel>?>(null)
    val removeCityResult: Flow<Resource<CityModel>?> get() = _removeCityResult
    private val _removeCityRequest = MutableSharedFlow<CityModel>(extraBufferCapacity = 32)
    init {
        viewModelScope.launch {
            _removeCityRequest
                .collect { city ->
                    repository.removeCity(city.id)
                        .flowOn(Dispatchers.IO)
                        .collect { resource ->
                            _removeCityResult.value = resource
                        }
                }
        }
    }

    fun removeCity(city: CityModel) {
        _removeCityRequest.tryEmit(city)
    }


    private val _citySearchResult =
        MutableStateFlow<Resource<List<CityModel>>>(Resource.Success(emptyList()))
    val citySearchResult: Flow<Resource<List<CityModel>>> get() = _citySearchResult
    private val _citySearchKeyword = MutableStateFlow<String?>(null)
    init {
        /**
         * in this case until i add a terminal method, here collect()
         * the someFlow will never start. this is due its cold stream behaviour
         * i.e. when there is not terminal method or someone is not actively looking for the result
         * then don't compute
         * viewModelScore.launcher {
         *      someFlow.
         *      collect { }
         * }
         *
         * or
         *
         * the code does the same as above. here launchIn is important. without it the flow does not start
         * someFlow.
         * .onEach{ }
         * .launchIn(viewModelScope)
         *
         *
         */
        viewModelScope.launch {
            _citySearchKeyword
                .debounce(300) // receive next keyword after 300ms
                .distinctUntilChanged() // execute next flow iff keyword has changed
                .flatMapLatest { keyword ->
                    // flow returned from flatMapLater will be canceled when new flow start but older one not finished yet
                    repository.searchCity(keyword)
                }
                .collect { result ->
                    _citySearchResult.value = result
                }
        }
    }

    fun searchCity(keyword: String?) {
        Log.i(TAG, "update city search keyword=$keyword")
        _citySearchKeyword.value = keyword
    }

    private val _saveCityResult = MutableStateFlow<Resource<CityModel>?>(null)
    val saveCityResult: Flow<Resource<CityModel>?> get() = _saveCityResult
    private val _saveCityData = MutableStateFlow<CityModel?>(null)
    init {
        viewModelScope.launch {
            _saveCityData
                .filterNotNull()
                .flatMapLatest { city ->
                    repository.saveCity(city)
                }
                .flowOn(Dispatchers.IO)
                .collect { city ->
                    _saveCityResult.value = city
                }
        }
    }

    fun saveCity(city: CityModel) {
        _saveCityData.value = city
    }
}