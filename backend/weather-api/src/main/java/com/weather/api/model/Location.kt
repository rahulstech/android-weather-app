package com.weather.api.model

data class Location(
    val id: String,
    val name: String,
    val country: String,
    val lat: Float,
    val lon: Float,
    val tz_id: String? = null
)
