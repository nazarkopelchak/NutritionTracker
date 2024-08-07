package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.TotalNutrition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetTotalNutrition (
    private val dt: NutritionDatabase
) {

     fun execute(nutritions: List<Nutrition>): TotalNutrition {
         val totalNutrition = TotalNutrition()

         for (i in nutritions) {
             totalNutrition.totalCalories += i.calories
             totalNutrition.totalFat = i.fat?.let { totalNutrition.totalFat?.plus(it) }
             totalNutrition.totalProtein = i.protein?.let { totalNutrition.totalProtein?.plus(it) }
             totalNutrition.totalSugar = i.sugar?.let { totalNutrition.totalSugar?.plus(it) }
         }

         return totalNutrition
//        val flow = flow {
//            val totalNutrition = TotalNutrition()
//
//            dt.nutritionDao.getNutritions().onEach {nutr ->
//                nutr.onEach {
//                    println("TEST = " + it.calories)
//                    totalNutrition.totalCalories += it.calories
//                    totalNutrition.totalFat = it.fat?.let { value -> totalNutrition.totalFat?.plus(value) }
//                    totalNutrition.totalProtein = it.protein?.let { value -> totalNutrition.totalProtein?.plus(value) }
//                    totalNutrition.totalSugar = it.sugar?.let { value -> totalNutrition.totalSugar?.plus(value) }
//                }
//            }
//
//            emit(totalNutrition)
//        }

    }
}