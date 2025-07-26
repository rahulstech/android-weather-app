package rahulstech.android.weatherapp.ui.search

import rahulstech.android.weatherapp.databinding.ItemSavedCityBinding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import rahulstech.android.weatherapp.R
import rahulstech.weather.repository.model.CityModel

private val savedCitiesCallback = object : DiffUtil.ItemCallback<CityModel>() {
    override fun areItemsTheSame(oldItem: CityModel, newItem: CityModel): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CityModel, newItem: CityModel): Boolean = oldItem == newItem
}

class SavedCitiesViewHolder(
    val binding: ItemSavedCityBinding,
    val onClickDeleteCity: (CityModel) -> Unit
) : ViewHolder(binding.root) {

    private var cachedCity: CityModel? = null

    init {
        binding.btnDelete.setOnClickListener {
            cachedCity?.let { onClickDeleteCity(it) }
        }
    }

    fun bind(data: CityModel?, selection: Long) {
        cachedCity = data
        if (null == data) {
            binding.apply {
                markSelectedCity.visibility = View.INVISIBLE
                labelCityDetails.text = null
            }

        }
        else {
            binding.apply {
                if (data.id == selection) {
                    markSelectedCity.visibility = View.VISIBLE
                }
                else {
                    markSelectedCity.visibility = View.INVISIBLE
                }
                labelCityDetails.text = itemView.context.getString(R.string.text_city_name, data.name, data.country)
            }
        }
    }
}

class SavedCitiesAdapter(
    context: Context,
    val onClickDeleteCity: (city: CityModel)->Unit
) : ListAdapter<CityModel, SavedCitiesViewHolder>(savedCitiesCallback) {

    private val inflater = LayoutInflater.from(context)
    private var lastSelection: Long = 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelection(selection: Long) {
        lastSelection = selection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCitiesViewHolder {
        val binding = ItemSavedCityBinding.inflate(inflater, parent, false)
        return SavedCitiesViewHolder(binding, onClickDeleteCity)
    }

    override fun onBindViewHolder(holder: SavedCitiesViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data, lastSelection)
    }
}