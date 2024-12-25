package rahulstech.android.weatherapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.weather.api.WeatherClient
import com.weather.api.model.CurrentWeatherReport
import rahulstech.android.weatherapp.BuildConfig
import rahulstech.android.weatherapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private val TAG = HomeActivity::class.java.simpleName

    private var labelCity: TextView? = null

    private var labelTemperature: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        labelCity = findViewById(R.id.label_city)
        labelTemperature =  findViewById(R.id.label_temperature)


        WeatherClient(BuildConfig.WEATHER_API_KEY)
            .getWeatherService()?.getCurrentWeatherAsync("London")
            ?.enqueue(object : Callback<CurrentWeatherReport>{
                override fun onResponse(
                    call: Call<CurrentWeatherReport>,
                    res: Response<CurrentWeatherReport>
                ) {
                    if (res.isSuccessful) {
                        onCurrentWeatherReportFetched(res.body())
                    }
                    else {
                        onRequestFail(res.code(), res.errorBody()?.string())
                    }
                }

                override fun onFailure(call: Call<CurrentWeatherReport>, err: Throwable) {
                    Log.e(TAG, "current weather fetch error ", err)
                    Toast.makeText(this@HomeActivity, "can not fetch current weather", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun onCurrentWeatherReportFetched(report: CurrentWeatherReport?) {
        val city = report?.location?.name
        val tempC = report?.current?.temp_c

        Log.i(TAG,"city=$city tempC=$tempC")

        labelCity?.text = city
        labelTemperature?.text = tempC.toString()
    }

    private fun onRequestFail(resCode: Int, body: String?) {
        Log.e(TAG, "request failed with res-code=$resCode err-body: $body")
    }
}