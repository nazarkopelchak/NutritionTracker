package com.example.nutritiontracker.data.reposiitory

import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.data.remote.NutritionAPI
import com.example.nutritiontracker.data.remote.dto.NutritionDto
import com.example.nutritiontracker.data.remote.dto.toNutrition
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.repository.NutritionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NutritionRepositoryImpl @Inject constructor (
    private val api: NutritionAPI,
): NutritionRepository {

    override fun getNutrition(ingredient: String): Flow<Resource<Nutrition>> = flow {
        //return api.getNutritionData("087770f5", "d4a5d7cdd11009b7b2fc536d637feb67",ingredient)
        emit(Resource.Loading())

        try {
            val nutritionData = api.getNutritionData("087770f5", "d4a5d7cdd11009b7b2fc536d637feb67",ingredient).toNutrition()

            if (nutritionData.fat == null && nutritionData.sugar == null && nutritionData.protein == null) {
                emit(Resource.Error("Incorrect input. Please try again."))
            }
            else { emit(Resource.Success(nutritionData)) }

        } catch (e: HttpException) {
            emit(Resource.Error("Something went wrong. Couldn't reach the server. Please try again later."))
        } catch (e: IOException) {
            emit(Resource.Error("Something went wrong. Check your internet connection."))
        }
    }
}