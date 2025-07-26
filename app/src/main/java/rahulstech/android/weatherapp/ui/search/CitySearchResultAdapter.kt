package rahulstech.android.weatherapp.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.databinding.ItemCitySearchResultBinding
import rahulstech.weather.repository.model.CityModel

val citySearchCallback = object : DiffUtil.ItemCallback<CityModel>() {
    override fun areItemsTheSame(oldItem: CityModel, newItem: CityModel): Boolean = oldItem.remoteId == newItem.remoteId

    override fun areContentsTheSame(oldItem: CityModel, newItem: CityModel): Boolean = oldItem == newItem
}

class CitySearchResultViewHolder(
    val binding: ItemCitySearchResultBinding,
) : ViewHolder(binding.root) {

    fun bind(data: CityModel?) {
        if (null == data) {
            binding.apply {
                labelCityDetails.text = null
            }

        }
        else {
            binding.apply {
                labelCityDetails.text = itemView.context.getString(R.string.text_city_name, data.name, data.country)
            }
        }
    }
}

class CitySearchResultAdapter(
    context: Context
) : ListAdapter<CityModel, CitySearchResultViewHolder>(citySearchCallback) {

    private val inflater = LayoutInflater.from(context)
    private var lastSelection: Long = 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelection(selection: Long) {
        lastSelection = selection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySearchResultViewHolder {
        val binding = ItemCitySearchResultBinding.inflate(inflater, parent, false)
        return CitySearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitySearchResultViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
}