package com.nazarkopelchak.nutritiontracker.domain.use_case

import com.nazarkopelchak.nutritiontracker.data.local.NutritionDatabase
import com.nazarkopelchak.nutritiontracker.domain.model.RecentNutrition
import javax.inject.Inject

class InsertLocalRecentNutritionData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(nutritionData: RecentNutrition) {
        dt.recentNutritionDao.insertNutrition(nutritionData.toRecentNutritionsEntity())
    }
}