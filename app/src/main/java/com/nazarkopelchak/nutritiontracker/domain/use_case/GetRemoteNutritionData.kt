package com.nazarkopelchak.nutritiontracker.domain.use_case

import com.nazarkopelchak.nutritiontracker.common.Resource
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import com.nazarkopelchak.nutritiontracker.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetRemoteNutritionData @Inject constructor(
    private val repository: NutritionRepository,
) {

    operator fun invoke(ingredient: String): Flow<Resource<Nutrition>> {
        if (ingredient.isBlank()) {
            return flow {  }
        }

        return repository.getNutrition(ingredient)
    }
}