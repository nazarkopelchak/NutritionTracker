package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.Nutrition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNutritionLocalData @Inject constructor(
    private val dt: NutritionDatabase
) {

    operator fun invoke(): Flow<List<Nutrition>> {
        return dt.nutritionDao.getNutritions().map { nutritions ->
            nutritions.map {
                it.toNutrition()
            }
        }
    }
}