package rahulstech.android.weatherapp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import rahulstech.android.weatherapp.databinding.HourlyForecastItemBinding
import rahulstech.android.weatherapp.util.get12HourFormattedTimeText
import rahulstech.android.weatherapp.util.getTemperatureCelsiusText
import rahulstech.android.weatherapp.util.getWeatherConditionIcon
import rahulstech.android.weatherapp.util.getWeatherConditionText
import rahulstech.weather.repository.model.HourWeatherModel

class HourlyForecastViewHolder(
    private val binding: HourlyForecastItemBinding
): ViewHolder(binding.root) {

//    private val labelTime: TextView = view.findViewById(R.id.label_time)
//    private val iconWeatherCondition: ImageView = view.findViewById(R.id.icon_weather_condition)
//    private val labelTemperature: TextView = view.findViewById(R.id.label_temperature)
//    private val labelCondition: TextView = view.findViewById(R.id.label_weather_condition)

    fun bind(data: HourWeatherModel?) {
        if (null == data) {
            binding.apply {
                labelTime.text = null
                iconWeatherCondition.setIconResource(null)
                labelTemperature.text = null
                labelWeatherCondition.text = null
            }
        }
        else {
            val context = itemView.context
            binding.apply {
                labelTime.text = get12HourFormattedTimeText(data.datetime.toLocalTime())
                iconWeatherCondition.setIconResource(getWeatherConditionIcon(context, data.conditionIconCode, data.isDay))
                labelTemperature.text = getTemperatureCelsiusText(data.temperatureC)
                labelWeatherCondition.text = getWeatherConditionText(context, data.conditionCode)
            }
        }
    }
}

val callback = object : DiffUtil.ItemCallback<HourWeatherModel>() {
    override fun areItemsTheSame(oldItem: HourWeatherModel, newItem: HourWeatherModel): Boolean = oldItem.datetime.isEqual(newItem.datetime)

    override fun areContentsTheSame(oldItem: HourWeatherModel, newItem: HourWeatherModel): Boolean =
        oldItem == newItem
}

class HourlyForecastAdapter(context: Context)
    : ListAdapter<HourWeatherModel, HourlyForecastViewHolder>(callback) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val binding = HourlyForecastItemBinding.inflate(inflater, parent, false)
        return HourlyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}