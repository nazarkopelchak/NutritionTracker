package com.nazarkopelchak.nutritiontracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nazarkopelchak.nutritiontracker.domain.model.Meals
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition

@Entity
data class NutritionEntity(
    val meal: Meals,
    val foodName: String,
    val amount: Double?,
    val measure: String,
    val calories: Int,
    val fat: Double?,
    val sugar: Double?,
    val protein: Double?,
    @PrimaryKey val id: Int? = null
) {
    fun toNutrition(): Nutrition {
        return Nutrition(
            meal = meal,
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
