package rahulstech.android.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import rahulstech.android.weatherapp.BuildConfig
import rahulstech.weather.repository.City
import rahulstech.weather.repository.WeatherRepository

class CitySearchViewModel(app: Application) : AndroidViewModel(app) {

    private val repo by lazy { WeatherRepository(BuildConfig.WEATHER_API_KEY) }

    private val keywordLiveData = MutableLiveData<String>()

    val citySearchResult: LiveData<List<City>> by lazy { keywordLiveData.switchMap { repo.searchCity(it) } }

    fun changeKeyword(keyword: String)  {
        keywordLiveData.value = keyword
    }
}