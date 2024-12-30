package rahulstech.android.weatherapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.adapter.HourlyForecastAdapter
import rahulstech.android.weatherapp.getTemperatureCelsiusText
import rahulstech.android.weatherapp.getWeatherConditionIcon
import rahulstech.android.weatherapp.getWeatherConditionText
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.viewmodel.HomeViewModel
import rahulstech.weather.repository.WeatherCondition
import rahulstech.weather.repository.WeatherForecast
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private val TAG = HomeActivity::class.java.simpleName

    private val WEATHER_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMMM hh:mm a")

    private val WEATHER_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a")

    private lateinit var viewModel: HomeViewModel

    private lateinit var labelCity: TextView

    private lateinit var labelTemperature: TextView

    private lateinit var iconWeatherCondition: ImageView

    private lateinit var uvIndex: TextView

    private lateinit var precipitation: TextView

    private lateinit var humidity: TextView

    private lateinit var sunrise: TextView

    private lateinit var sunset: TextView

    private lateinit var weatherCondition: TextView

    private lateinit var otherTemp: TextView

    private lateinit var dateTime: TextView

    private lateinit var forecastHourly: RecyclerView

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        labelCity = findViewById(R.id.label_city)
        labelTemperature =  findViewById(R.id.label_temperature)
        iconWeatherCondition = findViewById(R.id.icon_weather_condition)
        uvIndex = findViewById(R.id.uv_index)
        precipitation = findViewById(R.id.precipitation)
        humidity = findViewById(R.id.humidity)
        sunrise = findViewById(R.id.sunrise)
        sunset = findViewById(R.id.sunset)
        weatherCondition = findViewById(R.id.weather_condition)
        otherTemp = findViewById(R.id.other_temp)
        dateTime = findViewById(R.id.dateTime)
        forecastHourly = findViewById(R.id.forecast_hourly)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hourlyForecastAdapter = HourlyForecastAdapter(this)

        forecastHourly.layoutManager = layoutManager
        forecastHourly.adapter = hourlyForecastAdapter

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.weatherToday.observe(this) { onCurrentWeatherReportFetched(it) }
    }

    override fun onStart() {
        super.onStart()

        val weatherLocationId = SettingsStorage.get(this).getWeatherLocationId()
        viewModel.setLocationId(weatherLocationId)
    }

    private fun onCurrentWeatherReportFetched(report: WeatherForecast?) {

        if (null == report) {
            hourlyForecastAdapter.submitList(null)
            return
        }

        val now = LocalTime.now()

        val city = report.city
        val day = report.day
        val hours = report.hours
        val current = hours.find { it.datetime.hour == now.hour }

        labelCity.text = "${city.name}, ${city.region}"
        dateTime.text = LocalDateTime.now().format(WEATHER_DATETIME_FORMATTER)
        sunrise.text = day.sunrise.format(WEATHER_TIME_FORMATTER)
        sunset.text = day.sunset.format(WEATHER_TIME_FORMATTER)
        otherTemp.text = resources.getString(R.string.text_other_temp_c, day.maxTemp, day.minTemp, current?.feelsLike)

        hourlyForecastAdapter.submitList(hours)

        current?.let {
            updateCurrentTemperature(it.temp)
            uvIndex.text = getUvLabel(it.uv)
            precipitation.text = String.format(Locale.ENGLISH, "%.2f%%", it.precipitation * 100)
            humidity.text = String.format(Locale.ENGLISH, "%.2f%%", it.humidity)
            updateWeatherCondition(current.condition, current.isDay)
        }
    }

    private fun updateCurrentTemperature(tempC: Float) {
        labelTemperature.text = getTemperatureCelsiusText(tempC)
    }

    private fun updateWeatherCondition(wc: WeatherCondition, isDay: Boolean) {
        weatherCondition.text = getWeatherConditionText(this, wc, isDay)
        iconWeatherCondition.setImageDrawable(getWeatherConditionIcon(this, wc, isDay))
    }

    private fun getUvLabel(uv: Float): String = resources.getString( when {
        uv <= 2 -> R.string.text_uv_low
        uv <= 5 -> R.string.text_uv_moderate
        uv <= 7 -> R.string.text_uv_high
        uv <= 10 -> R.string.text_uv_very_high
        else -> R.string.text_uv_extream
    })

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