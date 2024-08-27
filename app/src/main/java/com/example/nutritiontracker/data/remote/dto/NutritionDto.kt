package com.example.nutritiontracker.data.remote.dto

import com.example.nutritiontracker.domain.model.Nutrition
import java.math.BigDecimal
import java.math.RoundingMode

private val unitMap = mapOf(
    "gram" to "g",
    "pound" to "lb",
    "ounce" to "oz"
)

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
    val totalWeight: Double,
    val uri: String
)

fun NutritionDto.toNutrition() : Nutrition {
    val nutrition = ingredients.first().parsed
    return if (nutrition == null) {
        Nutrition(foodName = null)
    }
    else {
        val foodNameLowerCase = ingredients.first().parsed?.first()?.foodMatch
        Nutrition(
            foodName = foodNameLowerCase?.replaceFirst(foodNameLowerCase[0], foodNameLowerCase[0].uppercaseChar()),
            amount = ingredients.first().parsed?.first()?.quantity!!,
            measure = unitMap[ingredients.first().parsed?.first()?.measure] ?: "",
            calories = calories,
            fat = BigDecimal(totalNutrients?.FAT?.quantity ?: 0.0).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            sugar = BigDecimal(totalNutrients?.SUGAR?.quantity ?: 0.0).setScale(2, RoundingMode.HALF_EVEN).toDouble() ,
            protein = BigDecimal(totalNutrients?.PROCNT?.quantity ?: 0.0).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        )
    }
}
