package com.nazarkopelchak.nutritiontracker.domain.model

import androidx.compose.runtime.Immutable
import com.nazarkopelchak.nutritiontracker.data.local.entity.NutritionEntity

@Immutable
data class Nutrition(
    val meal: Meals = Meals.BREAKFAST,
    val foodName: String? = "",
    val amount: Double? = 0.0,
    val measure: String = "",
    val calories: Int = 0,
    val fat: Double? = null,
    val sugar: Double? = null,
    val protein: Double? = null,
    val id: Int? = null
) {
    fun toNutritionEntity(): NutritionEntity {
        return NutritionEntity(
            meal = meal,
            foodName = foodName ?: "",
            amount = amount,
            measure = measure,
            calories = calories,
            fat = fat,
            sugar = sugar,
            protein = protein,
            id = id
        )
    }
}
