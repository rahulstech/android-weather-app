package rahulstech.android.weatherapp.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import rahulstech.android.weatherapp.R
import rahulstech.weather.repository.WeatherCondition
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val FORMATTER_TIME_12HOURS = DateTimeFormatter.ofPattern( "hh:mm a")

private const val TEXT_UNIT_CELSIUS = "°c"

fun getWeatherConditionText(ctx: Context, code: Int): String {
    val resId = when (code) {
        1000 -> R.string.weather_1000
        1003 -> R.string.weather_1003
        1006 -> R.string.weather_1006
        1009 -> R.string.weather_1009
        1030 -> R.string.weather_1030
        1063 -> R.string.weather_1063
        1066 -> R.string.weather_1066
        1069 -> R.string.weather_1069
        1072 -> R.string.weather_1072
        1087 -> R.string.weather_1087
        1114 -> R.string.weather_1114
        1117 -> R.string.weather_1117
        1135 -> R.string.weather_1135
        1147 -> R.string.weather_1147
        1150 -> R.string.weather_1150
        1153 -> R.string.weather_1153
        1168 -> R.string.weather_1168
        1171 -> R.string.weather_1171
        1180 -> R.string.weather_1180
        1183 -> R.string.weather_1183
        1186 -> R.string.weather_1186
        1189 -> R.string.weather_1189
        1192 -> R.string.weather_1192
        1195 -> R.string.weather_1195
        1198 -> R.string.weather_1198
        1201 -> R.string.weather_1201
        1204 -> R.string.weather_1204
        1207 -> R.string.weather_1207
        1210 -> R.string.weather_1210
        1213 -> R.string.weather_1213
        1216 -> R.string.weather_1216
        1219 -> R.string.weather_1219
        1222 -> R.string.weather_1222
        1225 -> R.string.weather_1225
        1237 -> R.string.weather_1237
        1240 -> R.string.weather_1240
        1243 -> R.string.weather_1243
        1246 -> R.string.weather_1246
        1249 -> R.string.weather_1249
        1252 -> R.string.weather_1252
        1255 -> R.string.weather_1255
        1258 -> R.string.weather_1258
        1261 -> R.string.weather_1261
        1264 -> R.string.weather_1264
        1273 -> R.string.weather_1273
        1276 -> R.string.weather_1276
        1279 -> R.string.weather_1279
        1282 -> R.string.weather_1282
        else -> R.string.weather_1000 // fallback to "Clear"
    }
    return ctx.getString(resId)
}

enum class WeatherIcon(
    @StringRes private val dayIconResource: Int,
    @StringRes private val nightIconResource: Int
) {
    ICON_113(R.string.weather_icon_113, R.string.weather_icon_113_night),
    ICON_116(R.string.weather_icon_116, R.string.weather_icon_116_night),
    ICON_119(R.string.weather_icon_119, R.string.weather_icon_119_night),
    ICON_122(R.string.weather_icon_122, R.string.weather_icon_122_night),
    ICON_143(R.string.weather_icon_143, R.string.weather_icon_143_night),
    ICON_176(R.string.weather_icon_176, R.string.weather_icon_176_night),
    ICON_179(R.string.weather_icon_179, R.string.weather_icon_179_night),
    ICON_182(R.string.weather_icon_182, R.string.weather_icon_182_night),
    ICON_185(R.string.weather_icon_185, R.string.weather_icon_185_night),
    ICON_200(R.string.weather_icon_200, R.string.weather_icon_200_night),
    ICON_227(R.string.weather_icon_227, R.string.weather_icon_227_night),
    ICON_230(R.string.weather_icon_230, R.string.weather_icon_230_night),
    ICON_248(R.string.weather_icon_248, R.string.weather_icon_248_night),
    ICON_260(R.string.weather_icon_260, R.string.weather_icon_260_night),
    ICON_263(R.string.weather_icon_263, R.string.weather_icon_263_night),
    ICON_266(R.string.weather_icon_266, R.string.weather_icon_266_night),
    ICON_281(R.string.weather_icon_281, R.string.weather_icon_281_night),
    ICON_284(R.string.weather_icon_284, R.string.weather_icon_284_night),
    ICON_293(R.string.weather_icon_293, R.string.weather_icon_293_night),
    ICON_296(R.string.weather_icon_296, R.string.weather_icon_296_night),
    ICON_299(R.string.weather_icon_299, R.string.weather_icon_299_night),
    ICON_302(R.string.weather_icon_302, R.string.weather_icon_302_night),
    ICON_305(R.string.weather_icon_305, R.string.weather_icon_305_night),
    ICON_308(R.string.weather_icon_308, R.string.weather_icon_308_night),
    ICON_311(R.string.weather_icon_311, R.string.weather_icon_311_night),
    ICON_314(R.string.weather_icon_314, R.string.weather_icon_314_night),
    ICON_317(R.string.weather_icon_317, R.string.weather_icon_317_night),
    ICON_320(R.string.weather_icon_320, R.string.weather_icon_320_night),
    ICON_323(R.string.weather_icon_323, R.string.weather_icon_323_night),
    ICON_326(R.string.weather_icon_326, R.string.weather_icon_326_night),
    ICON_329(R.string.weather_icon_329, R.string.weather_icon_329_night),
    ICON_332(R.string.weather_icon_332, R.string.weather_icon_332_night),
    ICON_335(R.string.weather_icon_335, R.string.weather_icon_335_night),
    ICON_338(R.string.weather_icon_338, R.string.weather_icon_338_night),
    ICON_350(R.string.weather_icon_350, R.string.weather_icon_350_night),
    ICON_353(R.string.weather_icon_353, R.string.weather_icon_353_night),
    ICON_356(R.string.weather_icon_356, R.string.weather_icon_356_night),
    ICON_359(R.string.weather_icon_359, R.string.weather_icon_359_night),
    ICON_362(R.string.weather_icon_362, R.string.weather_icon_362_night),
    ICON_365(R.string.weather_icon_365, R.string.weather_icon_365_night),
    ICON_368(R.string.weather_icon_368, R.string.weather_icon_368_night),
    ICON_371(R.string.weather_icon_371, R.string.weather_icon_371_night),
    ICON_374(R.string.weather_icon_374, R.string.weather_icon_374_night),
    ICON_377(R.string.weather_icon_377, R.string.weather_icon_377_night),
    ICON_386(R.string.weather_icon_386, R.string.weather_icon_386_night),
    ICON_389(R.string.weather_icon_389, R.string.weather_icon_389_night),
    ICON_392(R.string.weather_icon_392, R.string.weather_icon_392_night),
    ICON_395(R.string.weather_icon_395, R.string.weather_icon_395_night);

    fun getIcon(context: Context, isDay: Boolean = true): String {
        return context.getString(if (isDay) dayIconResource else nightIconResource)
    }

    companion object {
        fun fromCode(code: Int): WeatherIcon? {
            return entries.find { it.name == "ICON_$code" }
        }
    }
}

fun getWeatherConditionIcon(ctx: Context, code: Int, isDay: Boolean = true): String {
    val icon = WeatherIcon.fromCode(code) ?: WeatherIcon.ICON_113
    return icon.getIcon(ctx, isDay)
}

fun getTemperatureCelsiusText(tempC: Int): String = "$tempC $TEXT_UNIT_CELSIUS"

fun get12HourFormattedTimeText(time: LocalTime?): CharSequence? = time?.format(FORMATTER_TIME_12HOURS)

fun getUVLabel(ctx: Context, uv: Float): String = ctx.getString( when {
        uv <= 2 -> R.string.uv_low
        uv <= 5 -> R.string.uv_moderate
        uv <= 7 -> R.string.uv_high
        uv <= 10 -> R.string.uv_very_high
        else -> R.string.uv_extreme
    })


internal fun View.showView() {
    visibility = View.VISIBLE
}

internal fun View.hideView() {
    visibility = View.GONE
}

internal fun View.invisible() {
    visibility = View.INVISIBLE
}