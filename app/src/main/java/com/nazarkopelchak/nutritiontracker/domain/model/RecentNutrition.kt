package com.nazarkopelchak.nutritiontracker.domain.model

import androidx.compose.runtime.Immutable
import com.nazarkopelchak.nutritiontracker.data.local.entity.RecentNutritionsEntity
import java.time.LocalDate

@Immutable
data class RecentNutrition(
    val date: LocalDate,
    val listOfNutrition: List<Nutrition>,
    val calories: Int,
    val fat: Double,
    val protein: Double,
    val sugar: Double
) {
    fun toRecentNutritionsEntity(): RecentNutritionsEntity {
        return RecentNutritionsEntity(
            date = date,
            listOfNutrition = listOfNutrition,
            calories = calories,
            fat = fat,
            protein = protein,
            sugar = sugar
        )
    }
}
