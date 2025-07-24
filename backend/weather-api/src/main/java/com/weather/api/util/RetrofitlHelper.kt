package com.weather.api.util

import android.util.Log
import com.weather.api.WeatherClient
import com.weather.api.model.ErrorResponse
import retrofit2.Response
import retrofit2.Retrofit

fun <T> Response<T>.parseErrorBody(): ErrorResponse? {
    val gson = WeatherClient.createGson()
    return try {
        errorBody()?.charStream()?.use { reader ->
            gson.fromJson(reader, ErrorResponse::class.java)
        }
    }
    catch (ex: Exception) {
        Log.d("RetrofitHelper", "parseErrorBody failed with exception", ex)
        null
    }
}