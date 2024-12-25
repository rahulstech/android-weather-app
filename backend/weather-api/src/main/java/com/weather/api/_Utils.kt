package com.weather.api

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

fun convertStringToLocalDateTime(text: String?): LocalDateTime? {
    if (text.isNullOrBlank()) {
        return null;
    }
    return LocalDateTime.parse(text, DATE_TIME_FORMAT)
}

fun convertStringToZonedLocalDateTime(text: String?, tz: String?): ZonedDateTime? {
    val datetime: LocalDateTime = convertStringToLocalDateTime(text) ?: return null
    val zone = if (tz.isNullOrBlank()) ZoneId.systemDefault() else ZoneId.of(tz)
    return datetime.atZone(zone)
}