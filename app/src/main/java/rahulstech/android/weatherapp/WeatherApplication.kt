package rahulstech.android.weatherapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import rahulstech.weather.repository.weatherRepositoryModules

class WeatherApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WeatherApplication)

            modules(weatherRepositoryModules, appModule)
        }
    }
}