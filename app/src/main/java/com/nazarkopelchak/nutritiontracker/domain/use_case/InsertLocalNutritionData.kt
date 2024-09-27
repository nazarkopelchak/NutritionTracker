package com.nazarkopelchak.nutritiontracker.domain.use_case

import com.nazarkopelchak.nutritiontracker.data.local.NutritionDatabase
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import javax.inject.Inject

class InsertLocalNutritionData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(nutritionData: Nutrition) {
        dt.nutritionDao.insertNutrition(nutritionData.toNutritionEntity())
    }
}