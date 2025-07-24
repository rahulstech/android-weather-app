package rahulstech.android.weatherapp.setting

import android.content.Context
import android.util.Log
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

    private fun readLong(key: String, defaultValue: Long = 0): Long = sharedPreferences.getLong(key, defaultValue)

    private fun writeLong(key: String, value: Long) {
        sharedPreferences.edit(true) { putLong(key,value) }
    }

//    fun setWeatherLocationId(locationId: String) {
//        writeString(KEY_WEATHER_LOCATION_ID, locationId)
//    }
//
//    fun getWeatherLocationId(): String {
//        return readString(KEY_WEATHER_LOCATION_ID, DEFAULT_LOCATION_ID) as String
//    }

    fun setWeatherLocationId(locationId: Long) {
        Log.i("SettingsStorage", "locationId=$locationId")
        writeLong(KEY_WEATHER_LOCATION_ID, locationId)
    }

    fun getWeatherLocationId(): Long = readLong(KEY_WEATHER_LOCATION_ID, 0)

    companion object {

        fun get(context: Context) = SettingsStorage(context)
    }
}