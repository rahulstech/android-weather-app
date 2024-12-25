package com.weather.api.model

import com.google.gson.annotations.SerializedName

data class AQI(
    val co: Double, val no2: Double, val o3: Double, val so2: Float,
    val pm2_5: Float, val pm10: Float,
    @SerializedName("us-epa-index") val usEpaIndex: Int,
    @SerializedName("gb-defra-index") val gbDefraIndex: Int
)
