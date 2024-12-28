package rahulstech.android.weatherapp

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import rahulstech.weather.repository.WeatherCondition
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val FORMATTER_TIME_12HOURS = DateTimeFormatter.ofPattern( "hh:mm a")

val TEXT_UNIT_CELSIUS = "°c"

fun getWeatherConditionText(ctx: Context, wc: WeatherCondition, isDay: Boolean): CharSequence = ctx.getText(when (wc) {
    WeatherCondition.Fine -> if (isDay) R.string.text_sunny else R.string.text_clear
    WeatherCondition.Fog -> R.string.text_fog
    WeatherCondition.Mist -> R.string.text_mist
    WeatherCondition.Cloudy -> R.string.text_cloudy
    WeatherCondition.Rainy -> R.string.text_rainy
    WeatherCondition.Thunder -> R.string.text_thunder
    WeatherCondition.Snow -> R.string.text_snowfall
    WeatherCondition.Sleet -> R.string.text_sleet
    WeatherCondition.Blizzard -> R.string.text_blizzard
})

fun getWeatherConditionIcon(ctx: Context, wc: WeatherCondition, isDay: Boolean): Drawable = AppCompatResources. getDrawable(ctx, when (wc) {
    WeatherCondition.Fine -> if (isDay) R.drawable.sun else R.drawable.moon
    WeatherCondition.Fog -> R.drawable.fog
    WeatherCondition.Mist -> R.drawable.mist
    WeatherCondition.Cloudy -> R.drawable.cloud
    WeatherCondition.Rainy -> R.drawable.rain
    WeatherCondition.Thunder -> R.drawable.thunder
    WeatherCondition.Snow -> R.drawable.snowfall
    WeatherCondition.Sleet -> R.drawable.sleet
    WeatherCondition.Blizzard -> R.drawable.blizzard
}) as Drawable

fun getTemperatureCelsiusText(tempC: Float): String = String.format(Locale.ENGLISH, "%.2f $TEXT_UNIT_CELSIUS", tempC)

fun get12HourFormattedTimeText(time: LocalTime?): CharSequence? = time?.format(FORMATTER_TIME_12HOURS)