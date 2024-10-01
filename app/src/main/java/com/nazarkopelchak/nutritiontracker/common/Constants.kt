package com.nazarkopelchak.nutritiontracker.common

object Constants {
    const val APP_NAME = "NUTRITION_TRACKER"

    const val WORKER_TAG = "RECENT_DATA_WORKER"

    const val BASE_URL = "https://api.edamam.com"
    const val NUTRITION_CALCULATOR_URL = "https://www.calculator.net/macro-calculator.html"

    //Shared preference keys
    const val FIRST_START = "FIRST_START"
    const val MAX_CALORIES = "MAX_CALORIES"
    const val MAX_PROTEIN = "MAX_PROTEIN"
    const val MAX_SUGAR = "MAX_SUGAR"
    const val MAX_FAT = "MAX_FAT"
    const val RESET_TIME_ENABLED = "RESET_TIME_ENABLED"
    const val RESET_TIME = "RESET_TIME"

    const val GRAM_UNITS = "g"
    const val OUNCE_UNITS = "oz"
    const val POUND_UNITS = "lb"
    const val ITEMS = "items"
    val dropDownItems = listOf(
        GRAM_UNITS,
        OUNCE_UNITS,
        POUND_UNITS,
        ITEMS
    )
}