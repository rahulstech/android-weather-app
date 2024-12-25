package com.weather.api.model

import com.weather.api.convertStringToZonedLocalDateTime
import java.time.ZonedDateTime

data class Location(
    val name: String, val region: String, val country: String,
    val lat: Double, val lon: Double,
    val tz_id: String, val localtime: String
) {
    val currentTime: ZonedDateTime = convertStringToZonedLocalDateTime(localtime,tz_id) as ZonedDateTime
}
