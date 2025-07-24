package com.weather.api.util

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GsonHelperTest {

    @Test
    fun testParseLocalDate() {
        val deserializer = LocalDateType()
        val actual = deserializer.parse("2025-07-08")
        val expected = LocalDate.of(2025,7,8)
        assertEquals(expected,actual)
    }

    @Test
    fun testParseLocalTime() {
        val deserializer = LocalTimeType()
        val actual = deserializer.parse("05:03 pm")
        val expected = LocalTime.of(17,3)
        assertEquals(expected,actual)
    }

    @Test
    fun testParseLocalDateTime() {
        val deserializer = LocalDateTimeType()
        val actual = deserializer.parse("2025-07-08 17:25")
        val expected = LocalDateTime.of(2025,7,8,17,25)
        assertEquals(expected,actual)
    }
}