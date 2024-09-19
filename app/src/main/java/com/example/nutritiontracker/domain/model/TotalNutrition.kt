package com.example.nutritiontracker.domain.model

data class TotalNutrition(
    var totalCalories: Int = 0,
    var totalFat: Double = 0.0,
    var totalProtein: Double = 0.0,
    var totalSugar: Double = 0.0
)