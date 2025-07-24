package com.weather.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.weather.api.util.BooleanType
import com.weather.api.util.LocalDateTimeType
import com.weather.api.util.LocalDateType
import com.weather.api.util.LocalTimeType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class WeatherClient(private val key: String, private val baseurl: String = BASE_URL) {

    companion object {
        private val TAG = WeatherClient::class.java.simpleName

        private const val BASE_URL = "https://api.weatherapi.com/v1/"

        fun createGson(): Gson {
            val gson =  GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeType())
                .registerTypeAdapter(LocalDate::class.java, LocalDateType())
                .registerTypeAdapter(LocalTime::class.java, LocalTimeType())
                .registerTypeAdapter(Boolean::class.java, BooleanType())
                .create()

            return gson
        }
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

    private fun getGsonConverterFactory(): GsonConverterFactory {
        val gson = createGson()
        return GsonConverterFactory.create(gson)
    }

    val retrofit: Retrofit by lazy {
        val client = getOkHttpClient()
        val converterFactory = getGsonConverterFactory()
        val builder = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(converterFactory)
            .client(client)
        builder.build()
    }

    val api: WeatherApi by lazy { retrofit.create(WeatherApi::class.java) }
}