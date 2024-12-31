package rahulstech.android.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import rahulstech.android.weatherapp.BuildConfig
import rahulstech.weather.repository.WeatherForecast
import rahulstech.weather.repository.WeatherRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy { WeatherRepository(BuildConfig.WEATHER_API_KEY) }

    private val locationIdLiveData = MutableLiveData<String>()

    val weatherToday: LiveData<WeatherForecast?> = locationIdLiveData.switchMap {
        repository.getWeatherToday(it)
    }

    val sevenDaysWeatherForecast: LiveData<List<WeatherForecast>?> = locationIdLiveData.switchMap {
        repository.getWeatherForecast(it, 7)
    }

    fun setLocationId(locationId: String) {
        locationIdLiveData.value = locationId
    }
}
