package rahulstech.android.weatherapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.adapter.HourlyForecastAdapter
import rahulstech.android.weatherapp.databinding.ActivityHomeBinding
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.util.get12HourFormattedTimeText
import rahulstech.android.weatherapp.util.getUVLabel
import rahulstech.android.weatherapp.viewmodel.HomeViewModel
import rahulstech.weather.repository.WeatherCondition
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.CurrentWeatherModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import rahulstech.weather.repository.util.Resource
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private val TAG = HomeActivity::class.java.simpleName

    private val WEATHER_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMMM hh:mm a")

    private val WEATHER_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a")

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    private lateinit var binding: ActivityHomeBinding

    private val setting: SettingsStorage by lazy { SettingsStorage.get(this@HomeActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWeatherForecast.setOnClickListener { handleWeatherForecastButton() }
        binding.forecastHourly.apply{
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = HourlyForecastAdapter(this@HomeActivity).also { hourlyForecastAdapter = it }
        }

        lifecycleScope.launch {
            viewModel.city
                .filterNotNull()
                .distinctUntilChanged()
                .flatMapLatest { cityResource ->
                    Log.i(TAG, "city resource ${cityResource.javaClass.simpleName}")
                    when {
                        cityResource is Resource.Success -> {
                            viewModel.dailyForecast
                                .filterNotNull()
                                .flatMapLatest { dailyForecastResource ->
                                    Log.i(TAG, "city resource ${dailyForecastResource.javaClass.simpleName}")
                                    when {
                                        dailyForecastResource is Resource.Success -> {
                                            viewModel.hourlyForecast.map { hourlyForecastsResource ->
                                                cityResource to Pair(dailyForecastResource,hourlyForecastsResource)
                                            }
                                        }
                                        else -> flowOf(cityResource to Pair(dailyForecastResource, Resource.Loading()))
                                    }
                                }
                        }
                        else -> flowOf(cityResource to Pair(Resource.Loading(), Resource.Loading()))
                    }
                }
                .collect { (cityResource, forecast) ->
                    onWeatherForecastLoaded(cityResource, forecast.first, forecast.second)
                }
        }
    }

    private fun handleWeatherForecastButton() {
        startActivity(Intent(this, WeatherForecastActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        val cityId = setting.getWeatherLocationId()
        viewModel.changeCurrentCity(cityId)
    }

    private fun onWeatherForecastLoaded(
        cityResource: Resource<CityModel>,
        dailyForecastResource: Resource<DayWeatherModel>,
        hourlyForecastsResource: Resource<List<HourWeatherModel>>
    ) {
        if (cityResource is Resource.Success) {
            cityResource.data?.let { city ->
                binding.labelCity.text = "${city.name}, ${city.country}"
            }

        }

        onDailyForecastFetched(cityResource, dailyForecastResource)
        onHourlyForecastFetched(hourlyForecastsResource)
    }

    private fun onDailyForecastFetched(cityResource: Resource<CityModel>, dailyForecastResource: Resource<DayWeatherModel>) {
        when {
            cityResource is Resource.Success && dailyForecastResource is Resource.Success -> {
                onDailyForecastLoaded(cityResource.data!!, dailyForecastResource.data!!)
            }
        }
    }

    private fun onDailyForecastLoaded(city: CityModel, dailyWeather: DayWeatherModel) {
        binding.apply {
            labelCity.text = "${city.name}, ${city.country}"
            uvIndex.text = getUVLabel(this@HomeActivity, dailyWeather.uv)
            humidity.text = String.format(Locale.ENGLISH, "%d%%", dailyWeather.humidity)
            precipitation.text = String.format(Locale.ENGLISH, "%.2f mm", dailyWeather.precipitation)
            sunrise.text = get12HourFormattedTimeText(dailyWeather.sunrise)
            sunset.text = get12HourFormattedTimeText(dailyWeather.sunset)
        }
    }


    private fun onCurrentWeatherFetched(currentWeatherResource: Resource<CurrentWeatherModel>?) {

        when(currentWeatherResource) {
            is Resource.Success<CurrentWeatherModel> -> {
                val currentWeather = currentWeatherResource.data
                binding.dateTime.text = currentWeather?.datetime?.format(WEATHER_DATETIME_FORMATTER)
            }
            is Resource.Error<*> -> {}
            is Resource.Loading<*> -> {}
            null -> {}
        }


//
//        val now = LocalTime.now()
//
//        val city = report.city
//        val day = report.day
//        val hours = report.hours
//        val current = hours.find { it.datetime.hour == now.hour }

//        labelCity.text = "${city.name}, ${city.region}"
//        dateTime.text = LocalDateTime.now().format(WEATHER_DATETIME_FORMATTER)
//        sunrise.text = day.sunrise.format(WEATHER_TIME_FORMATTER)
//        sunset.text = day.sunset.format(WEATHER_TIME_FORMATTER)
//        otherTemp.text = resources.getString(R.string.text_other_temp_c, day.maxTemp, day.minTemp, current?.feelsLike)

//        hourlyForecastAdapter.submitList(hours)

//        current?.let {
//            updateCurrentTemperature(it.temp)
////            uvIndex.text = getUvLabel(it.uv)
////            precipitation.text = String.format(Locale.ENGLISH, "%.2f%%", it.precipitation * 100)
////            humidity.text = String.format(Locale.ENGLISH, "%.2f%%", it.humidity)
//            updateWeatherCondition(current.condition, current.isDay)
//        }
    }

    private fun onHourlyForecastFetched(resource: Resource<List<HourWeatherModel>>?) {
        when(resource) {
            is Resource.Loading -> {}
            is Resource.Error<*> -> {}
            is Resource.Success<List<HourWeatherModel>> -> {
                onHourlyForecastsLoaded(resource.data ?: emptyList())
            }
            null -> {}
        }
    }

    private fun onHourlyForecastsLoaded(hourlyForecast: List<HourWeatherModel>) {
        Log.i(TAG, "hourly forecast ${hourlyForecast.size}")
        hourlyForecastAdapter.submitList(hourlyForecast)
    }

    private fun updateCurrentTemperature(tempC: Float) {
//        labelTemperature.text = getTemperatureCelsiusText(tempC)
    }

    private fun updateWeatherCondition(wc: WeatherCondition, isDay: Boolean) {
//        weatherCondition.text = getWeatherConditionText(this, wc, isDay)
//        iconWeatherCondition.setImageDrawable(getWeatherConditionIcon(this, wc, isDay))
    }

    private fun onRequestFail(resCode: Int, body: String?) {
        Log.e(TAG, "request failed with res-code=$resCode err-body: $body")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_city -> {
                handleSearchCity()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleSearchCity() {
        startActivity(Intent(this, CitySearchActivity::class.java))
    }
}