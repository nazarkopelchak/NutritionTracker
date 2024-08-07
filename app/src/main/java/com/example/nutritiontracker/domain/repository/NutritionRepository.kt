package com.example.nutritiontracker.domain.repository

import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.domain.model.Nutrition
import kotlinx.coroutines.flow.Flow

interface NutritionRepository {

    fun getNutrition(ingredient: String): Flow<Resource<Nutrition>>
}