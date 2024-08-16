package com.example.nutritiontracker.presentation.util

sealed class Units {
    data object grams: Units()
    data object ounces: Units()
    data object pounds: Units()
}
