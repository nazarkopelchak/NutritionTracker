package com.example.nutritiontracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.RecentNutrition
import java.time.LocalDate

@Entity
data class RecentNutritionsEntity(
    @PrimaryKey val date: LocalDate,
    val listOfNutrition: List<Nutrition>,
    val calories: Int,
    val fat: Double,
    val protein: Double,
    val sugar: Double
) {
    fun toRecentNutritions(): RecentNutrition {
        return RecentNutrition(
            date = date,
            listOfNutrition = listOfNutrition,
            calories = calories,
            fat = fat,
            protein = protein,
            sugar = sugar
        )
    }
}
