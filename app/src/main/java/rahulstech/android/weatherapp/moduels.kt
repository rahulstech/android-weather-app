package rahulstech.android.weatherapp

import android.app.Application
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import rahulstech.android.weatherapp.viewmodel.CitySearchViewModel
import rahulstech.android.weatherapp.viewmodel.HomeViewModel
import rahulstech.android.weatherapp.viewmodel.WeatherViewModel

val appModule = module {
    viewModel { WeatherViewModel(get<Application>(), get()) }

    viewModel { CitySearchViewModel(get<Application>(), get()) }

    viewModel { HomeViewModel(get<Application>(), get()) }
}