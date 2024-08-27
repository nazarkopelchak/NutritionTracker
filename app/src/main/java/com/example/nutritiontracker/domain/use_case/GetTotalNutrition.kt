package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.TotalNutrition

class GetTotalNutrition () {

     fun execute(nutritions: List<Nutrition>): TotalNutrition {
         val totalNutrition = TotalNutrition()

         for (i in nutritions) {
             totalNutrition.totalCalories += i.calories
             totalNutrition.totalFat += i.fat ?: 0.0
             totalNutrition.totalProtein += i.protein ?: 0.0
             totalNutrition.totalSugar += i.sugar ?: 0.0
         }

         return totalNutrition
    }
}