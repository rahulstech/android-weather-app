package rahulstech.android.weatherapp.setting

import android.content.Context
import androidx.core.content.edit

class SettingsStorage private constructor(private val context: Context) {

    private val SHARED_PREFERENCE_NAME = "name.shared_preferences.weather"

    private val KEY_WEATHER_LOCATION_ID = "weather_location_id"

    private val DEFAULT_LOCATION_ID = "2801268"

    private val sharedPreferences by lazy { context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE) }

    private fun readString(key: String, defaultValue: String? = null): String? =
        sharedPreferences.getString(key, defaultValue)

    private fun writeString(key: String, value: String?) =
        sharedPreferences.edit(true) { putString(key, value) }

    fun setWeatherLocationId(locationId: String) {
        writeString(KEY_WEATHER_LOCATION_ID, locationId)
    }

    fun getWeatherLocationId(): String {
        return readString(KEY_WEATHER_LOCATION_ID, DEFAULT_LOCATION_ID) as String
    }

    companion object {

        fun get(context: Context) = SettingsStorage(context)
    }
}