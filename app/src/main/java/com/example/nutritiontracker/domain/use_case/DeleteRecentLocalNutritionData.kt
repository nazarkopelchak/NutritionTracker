package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.RecentNutritions
import javax.inject.Inject

class DeleteRecentLocalNutritionData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(nutritionData: RecentNutritions) {
        dt.recentNutritionDao.deleteNutrition(nutritionData.toRecentNutritionsEntity())
    }
}