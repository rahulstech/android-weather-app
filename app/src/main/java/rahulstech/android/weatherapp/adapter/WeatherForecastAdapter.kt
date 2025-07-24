package rahulstech.android.weatherapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.util.getWeatherConditionIcon
import rahulstech.android.weatherapp.util.getWeatherConditionText
import rahulstech.weather.repository.WeatherForecast
import java.time.format.DateTimeFormatter

private val FORMAT_FORECAST_DAY = DateTimeFormatter.ofPattern("EEEE, dd MMM")

class WeatherForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val context = view.context
    private val iconWeatherCondition: ImageView = view.findViewById(R.id.icon_weather_condition)
    private val labelWeatherCondition: TextView = view.findViewById(R.id.label_weather_condition)
    private val labelDate: TextView = view.findViewById(R.id.label_date)
    private val labelTemp: TextView = view.findViewById(R.id.label_temperature)
    private val labelPrecip: TextView = view.findViewById(R.id.label_precipitation)

    fun bind(data: WeatherForecast?) {
        if (null == data) {
            iconWeatherCondition.setImageDrawable(null)
            labelWeatherCondition.text = null
            labelDate.text = null
            labelTemp.text = null
            labelPrecip.text = null
        }
        else {
            val day = data.day
            val condition = day.condition

//            iconWeatherCondition.setImageDrawable(getWeatherConditionIcon(context, condition, true))
//            labelWeatherCondition.text = getWeatherConditionText(context, condition, true)
//            labelDate.text = day.date.format(FORMAT_FORECAST_DAY)
//            labelTemp.text = context.getString(R.string.text_forecast_temp_c, day.maxTemp, day.minTemp)
//            labelPrecip.text = context.getString(R.string.text_forecast_precipitation, day.totalPrecipitation)
        }
    }
}

private val weatherForecastItemCallback = object : DiffUtil.ItemCallback<WeatherForecast>() {
    override fun areItemsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean {
        return oldItem.city.locationId == newItem.city.locationId
                && oldItem.day.date.isEqual(newItem.day.date)
    }

    override fun areContentsTheSame(oldItem: WeatherForecast, newItem: WeatherForecast): Boolean =
        oldItem == newItem

}

class WeatherForecastAdapter(context: Context) :
    ListAdapter<WeatherForecast, WeatherForecastViewHolder>(weatherForecastItemCallback) {

        private val TAG = WeatherForecastAdapter::class.java.simpleName

        private val inflate = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherForecastViewHolder {
        val view = inflate.inflate(R.layout.item_daily_weather_forecast, parent, false)
        return WeatherForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        val item = getItem(position)
        Log.d(TAG, "binding forecast $item")
        holder.bind(item)
    }
}