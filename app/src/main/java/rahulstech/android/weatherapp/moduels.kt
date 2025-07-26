package rahulstech.android.weatherapp

import android.app.Application
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import rahulstech.android.weatherapp.ui.search.CitySearchViewModel
import rahulstech.android.weatherapp.ui.home.HomeViewModel
import rahulstech.android.weatherapp.ui.forecast.WeatherForecastViewModel

val appModule = module {
    viewModel { WeatherForecastViewModel(get<Application>(), get()) }

    viewModel { CitySearchViewModel(get<Application>(), get()) }

    viewModel { HomeViewModel(get<Application>(), get()) }
}