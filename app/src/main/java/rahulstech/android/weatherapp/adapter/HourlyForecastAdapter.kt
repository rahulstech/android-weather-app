package rahulstech.android.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.get12HourFormattedTimeText
import rahulstech.android.weatherapp.getTemperatureCelsiusText
import rahulstech.android.weatherapp.getWeatherConditionIcon
import rahulstech.android.weatherapp.getWeatherConditionText
import rahulstech.weather.repository.HourlyWeather

class HourlyForecastViewHolder(view: View) : ViewHolder(view) {

    private val labelTime: TextView = view.findViewById(R.id.label_time)
    private val iconWeatherCondition: ImageView = view.findViewById(R.id.icon_weather_condition)
    private val labelTemperature: TextView = view.findViewById(R.id.label_temperature)
    private val labelCondition: TextView = view.findViewById(R.id.label_weather_condition)

    fun bind(data: HourlyWeather?) {
        if (null == data) {
            labelTime.text = null
            iconWeatherCondition.setImageDrawable(null)
            labelTemperature.text = null
            labelCondition.text = null
        }
        else {
            val condition = data.condition
            val context = itemView.context

            labelTime.text = get12HourFormattedTimeText(data.datetime.toLocalTime())
            iconWeatherCondition.setImageDrawable(getWeatherConditionIcon(context, condition, data.isDay))
            labelTemperature.text = getTemperatureCelsiusText(data.temp)
            labelCondition.text = getWeatherConditionText(context, condition, data.isDay)
        }
    }
}

val callback = object : DiffUtil.ItemCallback<HourlyWeather>() {
    override fun areItemsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean = oldItem.datetime.isEqual(newItem.datetime)

    override fun areContentsTheSame(oldItem: HourlyWeather, newItem: HourlyWeather): Boolean = oldItem.equals(newItem)
}

class HourlyForecastAdapter(context: Context)
    : ListAdapter<HourlyWeather, HourlyForecastViewHolder>(callback) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val view = inflater.inflate(R.layout.hourly_forecast_item,parent,false)
        return HourlyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}