package com.weather.api

import com.weather.api.model.CurrentWeatherResponse
import com.weather.api.model.ForecastResponse
import com.weather.api.model.Location
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface WeatherApi {

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") q: String,
        @Query("days") days: Int
    ): Response<ForecastResponse>

//    suspend fun getForecastByCityId(
//        id: String,
//        days: Int
//    ): Response<ForecastResponse> = getForecast("id:$id", days)

    @GET("forecast.json")
    suspend fun getForecastForDate(
        @Query("q") q: String,
        @Query("dt") dt: String
    ): Response<ForecastResponse>

//    suspend fun getForecastForDateByCityId(
//        id: String,
//        date: LocalDate
//    ): Response<ForecastResponse> = getForecastForDate("id:$id", date.format(DateTimeFormatter.ISO_DATE))

    @GET("search.json")
    suspend fun searchCity(
        @Query("q") q: String
    ): Response<List<Location>>
}