package rahulstech.android.weatherapp.ui.forecast

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
import rahulstech.android.weatherapp.databinding.ItemDailyWeatherForecastBinding
import rahulstech.android.weatherapp.util.getTemperatureCelsiusText
import rahulstech.android.weatherapp.util.getWeatherConditionIcon
import rahulstech.android.weatherapp.util.getWeatherConditionText
import rahulstech.weather.repository.WeatherForecast
import rahulstech.weather.repository.model.DayWeatherModel
import java.time.format.DateTimeFormatter

private val FORMAT_FORECAST_DAY = DateTimeFormatter.ofPattern("EEEE, MMMM dd")

class WeatherForecastViewHolder(val binding: ItemDailyWeatherForecastBinding) : RecyclerView.ViewHolder(binding.root) {

    private val context = itemView.context

    fun bind(data: DayWeatherModel?) {
        if (null == data) {
            binding.apply {
                weatherConditionIconDay.setIconResource(null)
                labelDate.text = null
                labelWeatherCondition.text = null
                labelTemperature.text = null
            }

        }
        else {
            binding.apply {
                weatherConditionIconDay.setIconResource(
                    getWeatherConditionIcon(context, data.conditionIconCode, true)
                )
                labelWeatherCondition.text = getWeatherConditionText(context, data.conditionCode)
                labelDate.text = data.date.format(FORMAT_FORECAST_DAY)
                labelTemperature.text = context.getString(R.string.text_temp_min_max,
                    getTemperatureCelsiusText(data.temperatureMinC),
                    getTemperatureCelsiusText(data.temperatureMaxC)
                )
            }
        }
    }
}

private val weatherForecastItemCallback = object : DiffUtil.ItemCallback<DayWeatherModel>() {
    override fun areItemsTheSame(oldItem: DayWeatherModel, newItem: DayWeatherModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DayWeatherModel, newItem: DayWeatherModel): Boolean = oldItem == newItem
}

class WeatherForecastAdapter(context: Context) : ListAdapter<DayWeatherModel, WeatherForecastViewHolder>(weatherForecastItemCallback) {

    private val TAG = WeatherForecastAdapter::class.java.simpleName

    private val inflate = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherForecastViewHolder {
        val binding = ItemDailyWeatherForecastBinding.inflate(inflate, parent, false)
        return WeatherForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherForecastViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}