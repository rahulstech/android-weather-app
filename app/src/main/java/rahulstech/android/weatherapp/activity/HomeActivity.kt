package rahulstech.android.weatherapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import rahulstech.android.weatherapp.R
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

    private lateinit var labelTempUnit: TextView

    private lateinit var iconWeatherCondition: ImageView

    private lateinit var uvIndex: TextView

    private lateinit var precipitation: TextView

    private lateinit var humidity: TextView

    private lateinit var sunrise: TextView

    private lateinit var sunset: TextView

    private lateinit var weatherCondition: TextView

    private lateinit var otherTemp: TextView

    private lateinit var dateTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        labelCity = findViewById(R.id.label_city)
        labelTemperature =  findViewById(R.id.label_temperature)
        labelTempUnit = findViewById(R.id.label_temp_unit)
        iconWeatherCondition = findViewById(R.id.icon_weather_condition)
        uvIndex = findViewById(R.id.uv_index)
        precipitation = findViewById(R.id.precipitation)
        humidity = findViewById(R.id.humidity)
        sunrise = findViewById(R.id.sunrise)
        sunset = findViewById(R.id.sunset)
        weatherCondition = findViewById(R.id.weather_condition)
        otherTemp = findViewById(R.id.other_temp)
        dateTime = findViewById(R.id.dateTime)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Delhi: 1112321
        // Kandi: 1118644

        viewModel.weatherToday.observe(this) { onCurrentWeatherReportFetched(it) }
        viewModel.setLocationId("1112321")
    }

    private fun onCurrentWeatherReportFetched(report: WeatherForecast?) {

        if (null == report) {
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

        current?.let {
            labelTemperature.text = String.format(Locale.ENGLISH, "%.2f", it.temp)
            labelTempUnit.text = "°C"
            uvIndex.text = getUvLabel(it.uv)
            precipitation.text = String.format(Locale.ENGLISH, "%.2f%%", it.precipitation * 100)
            humidity.text = String.format(Locale.ENGLISH, "%.2f%%", it.humidity)

            updateWeatherCondition(current.condition, current.isDay)
        }
    }

    private fun updateWeatherCondition(wc: WeatherCondition, isDay: Boolean) {
        weatherCondition.text = wc.name
        iconWeatherCondition.setImageResource(when (wc) {
            WeatherCondition.Fine -> if (isDay) R.drawable.sun else R.drawable.moon
            WeatherCondition.Fog -> R.drawable.fog
            WeatherCondition.Mist -> R.drawable.mist
            WeatherCondition.Cloudy -> R.drawable.cloud
            WeatherCondition.Rainy -> R.drawable.rain
            WeatherCondition.Thunder -> R.drawable.thunder
            WeatherCondition.Snow -> R.drawable.snowfall
            WeatherCondition.Sleet -> R.drawable.sleet
            WeatherCondition.Blizzard -> R.drawable.blizzard
        })
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
}