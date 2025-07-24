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
import rahulstech.weather.repository.model.CityModel

val citySearchCallback = object : DiffUtil.ItemCallback<CityModel>() {
    override fun areItemsTheSame(oldItem: CityModel, newItem: CityModel): Boolean = oldItem.remoteId == newItem.remoteId

    override fun areContentsTheSame(oldItem: CityModel, newItem: CityModel): Boolean = oldItem == newItem
}

class CitySearchResultViewHolder(view: View) : ViewHolder(view) {

    private val labelCity: TextView = view.findViewById(R.id.label_city)
    private val labelCityDetails: TextView = view.findViewById(R.id.label_city_details)

    fun bind(data: CityModel?) {
        if (null == data) {
            labelCity.text = null
            labelCityDetails.text = null
        }
        else {
            labelCity.text = data.name
            labelCityDetails.text = "${data.name}, ${data.country}"
        }
    }
}

class CitySearchResultAdapter(context: Context) : ListAdapter<CityModel, CitySearchResultViewHolder>(citySearchCallback) {

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