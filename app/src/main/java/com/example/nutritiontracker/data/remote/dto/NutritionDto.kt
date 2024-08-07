package com.example.nutritiontracker.data.remote.dto

import com.example.nutritiontracker.domain.model.Nutrition

data class NutritionDto(
    val calories: Int,
    val cautions: List<String>?,
    val co2EmissionsClass: String?,
    val dietLabels: List<String>?,
    val healthLabels: List<String>,
    val ingredients: List<Ingredient>,
    val totalCO2Emissions: Double,
    val totalDaily: TotalDaily?,
    val totalNutrients: TotalNutrients?,
    val totalNutrientsKCal: TotalNutrientsKCal,
    val totalWeight: Int,
    val uri: String
)

fun NutritionDto.toNutrition() : Nutrition {
    return Nutrition(
        calories = calories,
        fat = totalNutrients?.FAT?.quantity,
        sugar = totalNutrients?.SUGAR?.quantity,
        protein = totalNutrients?.PROCNT?.quantity
    )
}