package com.example.nutritiontracker.domain.model

import java.time.LocalDate

data class TotalNutrition(
    var totalCalories: Int = 0,
    var totalFat: Double = 0.0,
    var totalProtein: Double = 0.0,
    var totalSugar: Double = 0.0
) {
    fun toRecentNutrition(): RecentNutrition {
        return RecentNutrition(
            date = LocalDate.now(),
            calories = totalCalories,
            fat = totalFat,
            protein = totalProtein,
            sugar = totalSugar
        )
    }
}