package com.weather.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherClient(apiKey: String) {

    private val TAG = WeatherClient::class.java.simpleName

    private val retrofit: Retrofit

    private var weatherService: WeatherService? = null

    init {

        val interceptor = Interceptor {
            val requestBuilder = it.request().newBuilder()
            val urlBuilder = it.request().url().newBuilder();
            val url = urlBuilder.addQueryParameter("key",apiKey).build()
            val request = requestBuilder.url(url).build()

            Log.d(TAG, "url= $url")

            it.proceed(request)
        }

        val httpClientBuilder = OkHttpClient.Builder()

        httpClientBuilder.interceptors().add(interceptor)

        val httpClient = httpClientBuilder.build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    fun getWeatherService(): WeatherService? {
        if (null == weatherService) {
            weatherService = retrofit.create(WeatherService::class.java)
        }
        return weatherService
    }
}