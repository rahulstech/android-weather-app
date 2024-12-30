package com.weather.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") q: String,
        @Query("days") days: Int
    ): Response<Forecast>

    @GET("search.json")
    suspend fun searchCity(
        @Query("q") q: String
    ): Response<List<Location>>
}