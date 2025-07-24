package com.weather.api.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

class LocalDateTimeType: JsonDeserializer<LocalDateTime?> {

    companion object {
        val FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        if (null == json|| json.isJsonNull) {
            return null
        }
        val datetimeText = json.asString
        return parse(datetimeText)
    }

    internal fun parse(datetimeText: String): LocalDateTime = LocalDateTime.parse(datetimeText, FORMAT_DATETIME)
}

class LocalDateType: JsonDeserializer<LocalDate?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        if (null == json|| json.isJsonNull) {
            return null
        }
        val dateText = json.asString
        return parse(dateText)
    }

    internal fun parse(dateText: String): LocalDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE)
}

class LocalTimeType: JsonDeserializer<LocalTime?> {

    companion object {
        private val FORMAT_TIME = DateTimeFormatterBuilder().parseCaseInsensitive()
            .appendPattern("hh:mm a").toFormatter(Locale.ENGLISH)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime? {
        if (null == json|| json.isJsonNull) {
            return null
        }
        val timeText = json.asString
        return parse(timeText)
    }

    internal fun parse(timeText: String): LocalTime = LocalTime.parse(timeText, FORMAT_TIME)
}

class BooleanType: JsonDeserializer<Boolean?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Boolean? {
        if (null == json || json.isJsonNull) {
            return null
        }
        val intVal = json.asInt
        return intVal == 1
    }
}