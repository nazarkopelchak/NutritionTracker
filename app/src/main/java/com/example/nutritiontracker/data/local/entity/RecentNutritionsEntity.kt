package com.example.nutritiontracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nutritiontracker.domain.model.RecentNutritions
import java.time.LocalDate

@Entity
data class RecentNutritionsEntity(
    val date: LocalDate,
    val calories: Int,
    val fat: Double?,
    val sugar: Double?,
    val protein: Double?,
    @PrimaryKey val id: Int? = null
) {
    fun toRecentNutritions(): RecentNutritions {
        return RecentNutritions(
            date = date,
            calories = calories,
            fat = fat,
            sugar = sugar,
            protein = protein
        )
    }
}
