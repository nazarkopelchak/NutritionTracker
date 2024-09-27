package com.nazarkopelchak.nutritiontracker.domain.use_case

import com.nazarkopelchak.nutritiontracker.data.local.NutritionDatabase
import com.nazarkopelchak.nutritiontracker.domain.model.RecentNutrition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentNutritionLocalData @Inject constructor(
    private val dt: NutritionDatabase
){

    operator fun invoke(): Flow<List<RecentNutrition>> {
        return dt.recentNutritionDao.getNutritions().map { nutritions ->
            nutritions.map {
                it.toRecentNutritions()
            }
        }
    }
}