package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.Nutrition
import javax.inject.Inject

class DeleteLocalNutritionData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(nutritionData: Nutrition) {
        dt.nutritionDao.deleteNutrition(nutritionData.toNutritionEntity())
    }
}