package rahulstech.weather.database.helper

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.sqlite.db.SupportSQLiteDatabase
import rahulstech.weather.database.WeatherDB

interface FakeData {
    fun insert(db: SupportSQLiteDatabase)
}

fun getFakeDataForVersion(version: Int = WeatherDB.DB_VERSION): FakeData {
    return when(version) {
        1 -> FakeData1()
        else -> error("unknown version $version")
    }
}

class FakeData1: FakeData {

    override fun insert(db: SupportSQLiteDatabase) {
        // add cities
        db.insert("weather_cities", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
            "id" to 1L,
            "name" to "London",
            "country" to "United Kingdom",
            "longitude" to 12.563f,
            "latitude" to 0.5645f,
            "remoteId" to "12356"
        ))

        db.insert("weather_cities", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
            "id" to 2L,
            "name" to "New York",
            "country" to "USA",
            "longitude" to 56.563f,
            "latitude" to 23.5645f,
            "remoteId" to "58774"
        ))

        // add day weather
        db.insert("weather_day", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
            "id" to 1L,
            "cityId" to 1L,
            "date" to "2025-07-01",
            "conditionCode" to 1000,
            "iconCode" to 113,
            "temperatureMaxC" to 28f,
            "temperatureMinC" to 23f,
            "precipitationMm" to 0.20f,
            "snowCm" to 0f,
            "humidity" to 43,
            "uv" to .2f,
            "sunrise" to "05:50",
            "sunset" to "18:50"
        ))

        db.insert("weather_day", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
            "id" to 2L,
            "cityId" to 1L,
            "date" to "2025-07-02",
            "conditionCode" to 1002,
            "iconCode" to 119,
            "temperatureMaxC" to 26f,
            "temperatureMinC" to 22f,
            "precipitationMm" to 0.30f,
            "snowCm" to 0f,
            "humidity" to 40,
            "uv" to .3f,
            "sunrise" to "05:51",
            "sunset" to "18:49"
        ))

        // add hour weather
        db.insert("weather_hourly", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
            "id" to 1L,
            "cityId" to 1L,
            "date" to "2025-07-01",
            "time" to "08:00",
            "isDay" to 1,
            "lastModified" to "2025-07-01 00:00",
            "conditionCode" to 1000,
            "iconCode" to 113,
            "temperatureC" to 23f,
            "temperatureFeelsLikeC" to 25f,
            "uv" to .2f,
        ))
        db.insert("weather_hourly", SQLiteDatabase.CONFLICT_FAIL, contentValuesOf(
            "id" to 2L,
            "cityId" to 1L,
            "date" to "2025-07-01",
            "time" to "12:00",
            "isDay" to 1,
            "lastModified" to "2025-07-01 00:00",
            "conditionCode" to 1001,
            "iconCode" to 115,
            "temperatureC" to 25f,
            "temperatureFeelsLikeC" to 27f,
            "uv" to .3f,
        ))
    }
}

