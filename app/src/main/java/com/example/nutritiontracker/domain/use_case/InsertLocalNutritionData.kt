package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.Nutrition
import javax.inject.Inject

class InsertLocalNutritionData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(nutritionData: Nutrition) {
        dt.nutritionDao.insertNutrition(nutritionData.toNutritionEntity())
    }
}