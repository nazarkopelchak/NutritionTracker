package com.example.nutritiontracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.TotalNutrition

@Entity
data class NutritionEntity(
    val foodName: String,
    val amount: Double,
    val measure: String,
    val calories: Int,
    val fat: Double?,
    val sugar: Double?,
    val protein: Double?,
    @PrimaryKey val id: Int? = null
) {
    fun toNutrition(): Nutrition {
        return Nutrition(
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

//    fun toTotalNutrition(): TotalNutrition {
//        return TotalNutrition(
//            totalCalories = calories,
//            totalFat = fat,
//            totalProtein = protein,
//            totalSugar = sugar
//        )
//    }
}
