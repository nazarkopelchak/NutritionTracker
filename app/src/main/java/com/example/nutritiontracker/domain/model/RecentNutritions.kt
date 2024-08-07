package com.example.nutritiontracker.domain.model

import com.example.nutritiontracker.data.local.entity.RecentNutritionsEntity
import java.time.LocalDate

data class RecentNutritions(
    val date: LocalDate,
    val calories: Int,
    val fat: Double? = null,
    val sugar: Double? = null,
    val protein: Double? = null
) {
    fun toRecentNutritionsEntity(): RecentNutritionsEntity {
        return RecentNutritionsEntity(
            date = date,
            calories = calories,
            fat = fat,
            sugar = sugar,
            protein = protein

        )
    }
}
