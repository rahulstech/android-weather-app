package rahulstech.weather.repository

import com.weather.api.WeatherClient
import org.koin.dsl.module
import rahulstech.weather.database.WeatherDB
import rahulstech.weather.repository.impl.DaoProviderImpl
import rahulstech.weather.repository.impl.WeatherRepositoryImpl

internal val weatherDbModules = module {
    single { WeatherDB.instance(get()) }
}

internal val weatherApiModules = module {
    single { WeatherClient(BuildConfig.WEATHER_API_KEY) }
    single { get<WeatherClient>().api }
}

val weatherRepositoryModules = module {

    // db and api modules are automatically included in repository module
    // so no need to manually add in startKoin {}
    includes(weatherDbModules, weatherApiModules)

    single<DaoProvider> { DaoProviderImpl(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
}