package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.domain.model.RecentNutritions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentNutritionLocalData @Inject constructor(
    private val dt: NutritionDatabase
){

    operator fun invoke(): Flow<List<RecentNutritions>> {
        return dt.recentNutritionDao.getNutritions().map { nutritions ->
            nutritions.map {
                it.toRecentNutritions()
            }
        }
    }
}