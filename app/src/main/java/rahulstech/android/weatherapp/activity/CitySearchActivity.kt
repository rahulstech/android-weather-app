package rahulstech.android.weatherapp.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.adapter.CitySearchResultAdapter
import rahulstech.android.weatherapp.helper.OnRecyclerViewItemClickListener
import rahulstech.android.weatherapp.helper.RecyclerViewItemClickHelper
import rahulstech.android.weatherapp.setting.SettingsStorage
import rahulstech.android.weatherapp.viewmodel.CitySearchViewModel
import rahulstech.weather.repository.City

class CitySearchActivity : AppCompatActivity(), OnRecyclerViewItemClickListener {

    private val TAG = CitySearchActivity::class.java.simpleName

    private lateinit var viewModel: CitySearchViewModel

    private lateinit var inputSearch: EditText

    private lateinit var searchResult: RecyclerView

    private lateinit var citySearchResultAdapter: CitySearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_search)

        inputSearch = findViewById(R.id.input_search)
        searchResult = findViewById(R.id.search_result)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val itemClickHelper = RecyclerViewItemClickHelper(searchResult)
        itemClickHelper.addOnRecyclerViewItemClickListener(this)
        citySearchResultAdapter = CitySearchResultAdapter(this)

        searchResult.layoutManager = layoutManager
        searchResult.adapter = citySearchResultAdapter
        searchResult.addOnItemTouchListener(itemClickHelper)

        viewModel = ViewModelProvider(this).get(CitySearchViewModel::class.java)
        viewModel.citySearchResult.observe(this) { onCitiesFetched(it) }

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString()
                if (keyword.length < 3 ) {
                    viewModel.changeKeyword(null)
                }
                else {
                    viewModel.changeKeyword(keyword)
                }
            }
        })
    }

    private fun onCitiesFetched(cities: List<City>) {
        citySearchResultAdapter.submitList(cities)
    }

    override fun onClickItem(recyclerView: RecyclerView, itemView: View, adapterPosition: Int) {
        val item = citySearchResultAdapter.currentList[adapterPosition]
        val locationId = item.locationId
        val location = "${item.name}, ${item.region}, ${item.country}"
        Log.i(TAG, "selected location: id=$locationId location= $location")
        SettingsStorage.get(this).setWeatherLocationId(locationId)
        Toast.makeText(this, getString(R.string.message_current_location_changed, location), Toast.LENGTH_SHORT).show()
        finish()
    }
}