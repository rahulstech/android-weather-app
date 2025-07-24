package com.weather.api.model

data class Condition(val icon: String, val code: Int) {

    val iconCode: Int
        get() {
            // Note: i can not run this code in init{} because the Condition instance is created via reflection
            // which does not guarantee to call the init{}
            val iconCodeText = icon.substringAfterLast("/").substringBefore(".")
            return iconCodeText.toInt()
        }
}
