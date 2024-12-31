package rahulstech.android.weatherapp.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.adapter.WeatherForecastAdapter
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.viewmodel.HomeViewModel
import rahulstech.weather.repository.WeatherForecast

class WeatherForecastActivity : AppCompatActivity() {

    private val TAG = WeatherForecastActivity::class.java.simpleName

    private lateinit var forecasts: RecyclerView

    private lateinit var weatherForecastAdapter: WeatherForecastAdapter

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_forecast)

        forecasts = findViewById(R.id.forecasts)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        forecasts.layoutManager = layoutManager

        weatherForecastAdapter = WeatherForecastAdapter(this)
        forecasts.adapter = weatherForecastAdapter

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.sevenDaysWeatherForecast.observe(this) { onForecastFetched(it) }
    }

    private fun onForecastFetched(data: List<WeatherForecast>?) {
        if (!data.isNullOrEmpty()) {
            val city = data[0].city
            this.title = "${city.name}, ${city.region}"
        }
        Log.i(TAG, "no. of forecasts ${data?.size}")
        weatherForecastAdapter.submitList(data)
    }

    override fun onStart() {
        super.onStart()

        val weatherLocationId = SettingsStorage.get(this).getWeatherLocationId()
        viewModel.setLocationId(weatherLocationId)
    }
}