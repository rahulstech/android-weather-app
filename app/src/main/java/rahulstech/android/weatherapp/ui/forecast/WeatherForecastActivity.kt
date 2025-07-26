package rahulstech.android.weatherapp.ui.forecast

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.databinding.ActivityWeatherForecastBinding
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.util.hideView
import rahulstech.android.weatherapp.util.showView
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.model.DayWeatherModel
import rahulstech.weather.repository.util.Resource

class WeatherForecastActivity : AppCompatActivity() {

    private val TAG = WeatherForecastActivity::class.java.simpleName

    private lateinit var weatherForecastAdapter: WeatherForecastAdapter

    private val viewModel: WeatherForecastViewModel by viewModel()

    private lateinit var binding: ActivityWeatherForecastBinding

    private val settings: SettingsStorage by lazy { SettingsStorage.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWeatherForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forecasts.apply {
            layoutManager = LinearLayoutManager(this@WeatherForecastActivity, LinearLayoutManager.VERTICAL, false)
            adapter = WeatherForecastAdapter(this@WeatherForecastActivity).also {
                weatherForecastAdapter = it
            }
        }

        lifecycleScope.launch {
            viewModel.cityForecasts
                .distinctUntilChanged()
                .collect { resources ->
                    onCityFetched(resources.first)
                    onForecastFetched(resources.second)
                }
        }
    }

    private fun onCityFetched(resource: Resource<CityModel>) {
        when(resource) {
            is Resource.Success -> {
                title = resource.data?.name ?: getString(R.string.activity_daily_forecast)
            }
            else -> {
                title = getString(R.string.activity_daily_forecast)
            }
        }
    }

    private fun onForecastFetched(resource: Resource<List<DayWeatherModel>>) {
        when(resource) {
            is Resource.Loading -> {
                binding.apply {
                    forecasts.hideView()
                    dailyForecastShimmer.apply {
                        startShimmer()
                        showView()
                    }
                }
            }
            is Resource.Success -> {
                onForecastLoaded(resource.data ?: emptyList())
                binding.apply {
                    dailyForecastShimmer.apply {
                        hideView()
                        stopShimmer()
                    }
                    forecasts.showView()
                }
            }
            else -> {}
        }
    }

    private fun onForecastLoaded(data: List<DayWeatherModel>) {
        Log.i(TAG, "no. of forecasts ${data.size}")
        weatherForecastAdapter.submitList(data)
    }

    override fun onResume() {
        super.onResume()
        val weatherLocationId = settings.getWeatherLocationId()
        viewModel.getForecast(weatherLocationId)
    }
}