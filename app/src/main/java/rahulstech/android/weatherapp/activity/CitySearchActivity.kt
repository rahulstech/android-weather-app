package rahulstech.android.weatherapp.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rahulstech.android.weatherapp.R
import rahulstech.android.weatherapp.adapter.CitySearchResultAdapter
import rahulstech.android.weatherapp.viewmodel.CitySearchViewModel
import rahulstech.weather.repository.City

class CitySearchActivity : AppCompatActivity() {

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
        citySearchResultAdapter = CitySearchResultAdapter(this)

        searchResult.layoutManager = layoutManager
        searchResult.adapter = citySearchResultAdapter

        viewModel = ViewModelProvider(this).get(CitySearchViewModel::class.java)
        viewModel.citySearchResult.observe(this) { onCitiesFetched(it) }

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.changeKeyword(s.toString())
            }
        })
    }

    private fun onCitiesFetched(cities: List<City>) {
        citySearchResultAdapter.submitList(cities)
    }
}