package rahulstech.android.weatherapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.databinding.ActivityHomeBinding
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.ui.forecast.WeatherForecastActivity
import rahulstech.android.weatherapp.ui.search.CitySearchActivity
import rahulstech.android.weatherapp.util.get12HourFormattedTimeText
import rahulstech.android.weatherapp.util.getTemperatureCelsiusText
import rahulstech.android.weatherapp.util.getUVLabel
import rahulstech.android.weatherapp.util.getWeatherConditionIcon
import rahulstech.android.weatherapp.util.getWeatherConditionText
import rahulstech.android.weatherapp.util.hideView
import rahulstech.android.weatherapp.util.invisible
import rahulstech.android.weatherapp.util.showView
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.model.HourWeatherModel
import rahulstech.weather.repository.util.Resource
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeActivity : AppCompatActivity() {

    companion object {
        private val TAG = HomeActivity::class.java.simpleName

        private val WEATHER_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMMM hh:mm a")
    }

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    private lateinit var binding: ActivityHomeBinding

    private val setting: SettingsStorage by lazy { SettingsStorage.Companion.get(this@HomeActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWeatherForecast.setOnClickListener { handleWeatherForecastButton() }
        binding.forecastHourly.apply{
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = HourlyForecastAdapter(this@HomeActivity).also { hourlyForecastAdapter = it }
        }

        lifecycleScope.launch {
            combine(
                viewModel.city,
                viewModel.dailyForecast,
                viewModel.hourlyForecast
            ) { cityResource, dailyForecastResource, hourlyForecastResource ->
                cityResource to Pair(dailyForecastResource, hourlyForecastResource)
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { (cityResource, forecasts) ->
                    onWeatherForecastFetched(cityResource, forecasts.first, forecasts.second)
                }
        }
    }

    private fun handleWeatherForecastButton() {
        startActivity(Intent(this, WeatherForecastActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        val cityId = setting.getWeatherLocationId()
        if (cityId > 0) {
            viewModel.changeCurrentCity(cityId)
        }
        else {
            showCityNotSelectedWarning()
        }
    }

    private fun showCityNotSelectedWarning() {
        AlertDialog.Builder(this)
            .setMessage(R.string.message_no_city_selected)
            .setPositiveButton(R.string.text_choose_city) { di,which -> handleSearchCity() }
            .setNeutralButton(R.string.text_exit) { di,which -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun onWeatherForecastFetched(
        cityResource: Resource<CityModel>,
        dailyForecastResource: Resource<DayWeatherModel>,
        hourlyForecastsResource: Resource<List<HourWeatherModel>>
    ) {
        onCurrentWeatherFetched(cityResource, dailyForecastResource, hourlyForecastsResource)
        onHourlyForecastFetched(hourlyForecastsResource)
        onDailyForecastFetched(dailyForecastResource)
    }

    private fun onCurrentWeatherFetched(
        cityResource: Resource<CityModel>,
        dailyForecastResource: Resource<DayWeatherModel>,
        hourlyForecastsResource: Resource<List<HourWeatherModel>>
    ) {
        when {
            cityResource is Resource.Success
                    && dailyForecastResource is Resource.Success
                    && hourlyForecastsResource is Resource.Success -> {

                        val city = cityResource.data ?: return
                        val dailyForecast = dailyForecastResource.data ?: return
                        val hourlyForecast = hourlyForecastsResource.data
                        if (!hourlyForecast.isNullOrEmpty()) {
                            onCurrentWeatherLoaded(city,dailyForecast,hourlyForecast)
                        }

                        binding.apply {
                            weatherSummaryShimmer.apply {
                                hideView()
                                stopShimmer()
                            }
                            weatherSummaryGroup.showView()
                        }
                    }
            cityResource is Resource.Loading
                    || dailyForecastResource is Resource.Loading
                    || hourlyForecastsResource is Resource.Loading -> {

                        binding.apply {
                            weatherSummaryGroup.invisible()
                            weatherSummaryShimmer.apply {
                                startShimmer()
                                showView()
                            }
                        }
                    }
            else -> {}
        }
    }

    private fun onCurrentWeatherLoaded(city: CityModel, dailyForecast: DayWeatherModel, hourlyForecast: List<HourWeatherModel>) {
        val currentHour = LocalTime.now().hour
        val currentWeather = hourlyForecast[currentHour]
        val datetime = LocalDateTime.now()
        binding.apply {
            labelCity.text = getString(R.string.text_city_name, city.name, city.country)
            labelDatetime.text = datetime.format(WEATHER_DATETIME_FORMATTER)
            weatherConditionIcon.setIconResource(
                getWeatherConditionIcon(
                    this@HomeActivity,
                    currentWeather.conditionIconCode,
                    currentWeather.isDay
                )
            )
            labelTemperature.text =
                getTemperatureCelsiusText(currentWeather.temperatureC)
            labelWeatherCondition.text = getWeatherConditionText(
                this@HomeActivity,
                currentWeather.conditionCode
            )
            textTempMinMax.text = getString(
                R.string.text_temp_min_max,
                getTemperatureCelsiusText(dailyForecast.temperatureMinC),
                getTemperatureCelsiusText(dailyForecast.temperatureMaxC),
            )
            textTempFeelsLike.text =
                getTemperatureCelsiusText(currentWeather.temperatureFeelLikeC)
        }
    }

    private fun onHourlyForecastFetched(resource: Resource<List<HourWeatherModel>>) {
        when(resource) {
            is Resource.Loading -> {
                // start the shimmer for hourly forecast
                binding.apply {
                    hourlyForecastGroup.invisible()
                    hourlyWeatherShimmerContent.apply {
                        startShimmer()
                        showView()
                    }
                }
            }
            is Resource.Error -> {}
            is Resource.Success -> {
                binding.apply {
                    hourlyWeatherShimmerContent.apply {
                        hideView()
                        stopShimmer()
                    }
                    hourlyForecastGroup.showView()
                }
                onHourlyForecastsLoaded(resource.data ?: emptyList())
            }
        }
    }

    private fun onHourlyForecastsLoaded(hourlyForecast: List<HourWeatherModel>) {
        Log.i(TAG, "hourly forecast ${hourlyForecast.size}")
        hourlyForecastAdapter.submitList(hourlyForecast)
    }

    private fun onDailyForecastFetched(dailyForecastResource: Resource<DayWeatherModel>) {
        when(dailyForecastResource){
            is Resource.Loading -> {
                binding.apply {
                    weatherDetailsContent.invisible()
                    weatherDetailsShimmer.apply {
                        startShimmer()
                        showView()
                    }
                }
            }
            is Resource.Success -> {
                onDailyForecastLoaded(dailyForecastResource.data!!)
                binding.apply {
                    weatherDetailsShimmer.apply {
                        hideView()
                        stopShimmer()
                    }
                    weatherDetailsContent.showView()
                }
            }
            else -> {}
        }
    }

    private fun onDailyForecastLoaded(dailyWeather: DayWeatherModel) {
        binding.apply {
            R.string.label_humidity
            uvIndex.text = getUVLabel(this@HomeActivity, dailyWeather.uv)
            humidity.text = getString(R.string.text_humidity_percentage, dailyWeather.humidity)
            precipitation.text = getString(R.string.text_precipitation_mm, dailyWeather.precipitation)
            sunrise.text = get12HourFormattedTimeText(dailyWeather.sunrise)
            sunset.text = get12HourFormattedTimeText(dailyWeather.sunset)
        }
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