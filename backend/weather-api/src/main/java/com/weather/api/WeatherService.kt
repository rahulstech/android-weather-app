package com.weather.api

import com.weather.api.model.CurrentWeatherReport
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("current.json")
    fun getCurrentWeatherAsync(
        @Query("q") query: String
    ): Call<CurrentWeatherReport>


}