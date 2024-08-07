package com.example.nutritiontracker.domain.model

import com.example.nutritiontracker.data.local.entity.NutritionEntity

data class Nutrition(
    val foodName: String = "",
    val amount: Double = 0.0,
    val measure: String = "",
    val calories: Int = 0,
    val fat: Double? = null,
    val sugar: Double? = null,
    val protein: Double? = null,
    val id: Int? = null
) {
    fun toNutritionEntity(): NutritionEntity {
        return NutritionEntity(
            foodName = foodName,
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
