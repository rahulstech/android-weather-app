package rahulstech.android.weatherapp.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.databinding.ActivityCitySearchBinding
import rahulstech.android.weatherapp.helper.OnRecyclerViewItemClickListener
import rahulstech.android.weatherapp.helper.RecyclerViewItemClickHelper
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.util.hideView
import rahulstech.android.weatherapp.util.showView
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.util.Resource

class CitySearchActivity : AppCompatActivity(), OnRecyclerViewItemClickListener {

    private val TAG = CitySearchActivity::class.java.simpleName

    private val viewModel: CitySearchViewModel by viewModel()

    private lateinit var citySearchResultAdapter: CitySearchResultAdapter

    private lateinit var savedCitiesAdapter: SavedCitiesAdapter

    private lateinit var binding: ActivityCitySearchBinding

    private val settings: SettingsStorage by lazy { SettingsStorage.Companion.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citySearchResultAdapter = CitySearchResultAdapter(this)
        savedCitiesAdapter = SavedCitiesAdapter(this, this::onClickDeleteCity)

        binding.citiesView.apply {
            layoutManager =
                LinearLayoutManager(this@CitySearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = savedCitiesAdapter
            addOnItemTouchListener(RecyclerViewItemClickHelper(binding.citiesView, intArrayOf(R.id.btn_delete)).apply {
                addOnRecyclerViewItemClickListener(this@CitySearchActivity)
            })
        }

        savedCitiesAdapter.updateSelection(settings.getWeatherLocationId())

        lifecycleScope.launch {
            viewModel.citySearchResult.collect { resource ->
                onCitiesFetched(resource, false)
            }
        }

        lifecycleScope.launch {
            viewModel.getAllCities().collect { resource ->
                onCitiesFetched(resource, true)
            }
        }

        lifecycleScope.launch {
            viewModel.saveCityResult
                .filterNotNull()
                .collect { onCitySaved(it) }
        }

        lifecycleScope.launch {
            viewModel.removeCityResult
                .filterNotNull()
                .collect {
                    onCityRemoved(it)
                }
        }

        binding.inputSearch.doAfterTextChanged { editable ->
            viewModel.searchCity(editable.toString())
        }
    }

    private fun onClickDeleteCity(city: CityModel) {
        Log.i(TAG, "delete city $city")
        viewModel.removeCity(city)
    }

    private fun onCitiesFetched(resource: Resource<List<CityModel>>, isFromCache: Boolean) {
        when (resource) {
            is Resource.Loading -> {
                val oldCities = resource.data
                if (isFromCache || oldCities.isNullOrEmpty()) {
                    binding.citiesShimmer.apply {
                        startShimmer()
                        showView()
                    }
                    binding.citiesView.hideView()
                } else {
                    citySearchResultAdapter.submitList(oldCities)
                    binding.citiesView.adapter = citySearchResultAdapter
                }
            }

            is Resource.Success -> {
                val cities = resource.data.orEmpty()
                binding.citiesView.adapter = if (isFromCache) {
                    savedCitiesAdapter.submitList(cities)
                     if (citySearchResultAdapter.currentList.isEmpty()) {
                        savedCitiesAdapter
                    }
                    else {
                        citySearchResultAdapter
                    }
                }
                else {
                    citySearchResultAdapter.submitList(cities)
                    if (cities.isEmpty()) {
                        savedCitiesAdapter
                    }
                    else {
                        citySearchResultAdapter
                    }
                }
                binding.apply {
                    citiesShimmer.apply {
                        stopShimmer()
                        hideView()
                    }
                    citiesView.showView()
                }
            }

            is Resource.Error -> {
                // Optional: show error UI or fallback
                binding.apply {
                    citiesShimmer.apply {
                        stopShimmer()
                        hideView()
                    }
                    citiesView.hideView()
                }
            }
        }
    }

    override fun onClickItem(recyclerView: RecyclerView, itemView: View, adapterPosition: Int) {
        (binding.citiesView.adapter as? ListAdapter<CityModel,*>)?.let { adapter ->
            val city = adapter.currentList[adapterPosition]
            Log.i(TAG, "selected city $city")
            when(adapter) {
                is SavedCitiesAdapter -> {
                    changeCurrentCity(city)
                    adapter.updateSelection(city.id)
                }
                is CitySearchResultAdapter -> {
                    viewModel.saveCity(city)
                }
            }
        }
    }

    private fun onCitySaved(resource: Resource<CityModel>) {
        when(resource) {
            is Resource.Loading -> {
                Toast.makeText(this, R.string.text_saving_city, Toast.LENGTH_LONG).show()
            }
            is Resource.Success -> {
                Log.i(TAG,"city saved successfully ${resource.data}")
                changeCurrentCity(resource.data!!)
            }
            is Resource.Error -> {
                Log.e(TAG,"city not saved", resource.error)
                Toast.makeText(this, R.string.text_city_not_saved, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeCurrentCity(city: CityModel) {
        Log.i(TAG, "change current city $city")
        settings.setWeatherLocationId(city.id)
        Toast.makeText(this, R.string.text_city_changed_successfully, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun onCityRemoved(resource: Resource<CityModel>) {
        when (resource) {
            is Resource.Loading -> {}
            is Resource.Error -> {
                Log.e(TAG, "city not removed", resource.error)
            }
            is Resource.Success -> {
                val removedCity = resource.data!!
                val currentCity = settings.getWeatherLocationId()
                if (currentCity == removedCity.id) {
                    settings.setWeatherLocationId(0)
                }
                Toast.makeText(this, getString(R.string.text_city_removed_successfully, removedCity.name), Toast.LENGTH_LONG).show()
            }
        }
    }
}