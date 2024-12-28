package rahulstech.android.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import rahulstech.android.weatherapp.R
import rahulstech.weather.repository.City

val citySearchCallback = object : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean = oldItem.locationId == newItem.locationId

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean = oldItem.equals(newItem)
}

class CitySearchResultViewHolder(view: View) : ViewHolder(view) {

    private val labelCity: TextView = view.findViewById(R.id.label_city)
    private val labelCityDetails: TextView = view.findViewById(R.id.label_city_details)

    fun bind(data: City?) {
        if (null == data) {
            labelCity.text = null
            labelCityDetails.text = null
        }
        else {
            labelCity.text = data.name
            labelCityDetails.text = "${data.region}, ${data.country}"
        }
    }
}

class CitySearchResultAdapter(context: Context) : ListAdapter<City, CitySearchResultViewHolder>(citySearchCallback) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySearchResultViewHolder {
        val view = inflater.inflate(R.layout.item_city_search_result, parent, false)
        return CitySearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitySearchResultViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}