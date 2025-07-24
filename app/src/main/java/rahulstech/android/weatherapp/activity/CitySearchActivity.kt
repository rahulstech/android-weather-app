package rahulstech.android.weatherapp.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.adapter.CitySearchResultAdapter
import rahulstech.android.weatherapp.databinding.ActivityCitySearchBinding
import rahulstech.android.weatherapp.helper.OnRecyclerViewItemClickListener
import rahulstech.android.weatherapp.helper.RecyclerViewItemClickHelper
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.viewmodel.CitySearchViewModel
import rahulstech.android.weatherapp.viewmodel.WeatherViewModel
import rahulstech.weather.repository.City
import rahulstech.weather.repository.model.CityModel
import rahulstech.weather.repository.util.Resource

class CitySearchActivity : AppCompatActivity(), OnRecyclerViewItemClickListener {

    private val TAG = CitySearchActivity::class.java.simpleName

    private val viewModel: CitySearchViewModel by viewModel()

    private lateinit var citySearchResultAdapter: CitySearchResultAdapter

    private lateinit var binding: ActivityCitySearchBinding

    private val settings: SettingsStorage by lazy { SettingsStorage.get(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchResult.apply {
            layoutManager = LinearLayoutManager(this@CitySearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = CitySearchResultAdapter(this@CitySearchActivity).also {
                citySearchResultAdapter = it
            }
            addOnItemTouchListener(RecyclerViewItemClickHelper(binding.searchResult).apply {
                addOnRecyclerViewItemClickListener(this@CitySearchActivity)
            })
        }

        lifecycleScope.launch {
            viewModel.citySearchResult.collect { resource ->
                when(resource) {
                    is Resource.Success -> {
                        onCitiesFetched(resource.data ?: emptyList())
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveCityResult.collect { onSaveCityResult(it) }
        }

        binding.inputSearch.doAfterTextChanged { editable ->
            viewModel.searchCity(editable.toString())
        }
    }

    private fun onCitiesFetched(cities: List<CityModel>) {
        citySearchResultAdapter.submitList(cities)
    }

    override fun onClickItem(recyclerView: RecyclerView, itemView: View, adapterPosition: Int) {
        val city = citySearchResultAdapter.currentList[adapterPosition]
        Log.i(TAG, "selected city $city")
        viewModel.saveCity(city)
    }

    private fun onSaveCityResult(result: Resource<CityModel>?) {
        when(result) {
            is Resource.Loading -> {
                Toast.makeText(this@CitySearchActivity, "saving city", Toast.LENGTH_LONG).show()
            }
            is Resource.Success -> {
                onCitySaved(result.data!!)
            }
            is Resource.Error -> {
                Toast.makeText(this@CitySearchActivity, "city not saved", Toast.LENGTH_LONG).show()
            }
            null -> {}
        }
    }

    private fun onCitySaved(city: CityModel) {
        Log.i(TAG, "set current city $city")
        settings.setWeatherLocationId(city.id)
        Toast.makeText(this@CitySearchActivity, "city saved successfully", Toast.LENGTH_LONG).show()
        finish()
    }
}