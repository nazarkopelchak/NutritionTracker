package com.nazarkopelchak.nutritiontracker.domain.repository

import com.nazarkopelchak.nutritiontracker.common.Resource
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {

    fun getNutrition(ingredient: String): Flow<Resource<Nutrition>>
}