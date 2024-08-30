package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.RecentNutrition
import javax.inject.Inject

class DeleteRecentLocalNutritionData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(nutritionData: RecentNutrition) {
        dt.recentNutritionDao.deleteNutrition(nutritionData.toRecentNutritionsEntity())
    }
}