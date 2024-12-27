package com.weather.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherClient private constructor() {

    private val TAG = WeatherClient::class.java.simpleName

    private val BASE_URL = "https://api.weatherapi.com/v1/"

    companion object {

        private lateinit var key: String

        private val instance: WeatherClient by lazy(this) { WeatherClient() }

        fun getInstance(key: String): WeatherClient {
            this.key = key
            return instance
        }
    }

    private val retrofit: Retrofit by lazy {

        val client = getOkHttpClient()

        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)

        builder.build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        val apikeyInterceptor = Interceptor {
            val ogreq = it.request()
            val ogurl = ogreq.url()
            val url = ogurl.newBuilder().addQueryParameter("key",key).build()
            val req = ogreq.newBuilder().url(url).build()
            it.proceed(req)
        }

        return OkHttpClient.Builder()
            .addInterceptor(apikeyInterceptor)
            .build()
    }


    val api: WeatherApi by lazy { retrofit.create(WeatherApi::class.java) }
}