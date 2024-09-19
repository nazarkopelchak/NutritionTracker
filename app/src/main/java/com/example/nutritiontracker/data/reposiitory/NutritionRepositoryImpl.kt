package com.example.nutritiontracker.data.reposiitory

import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.data.remote.NutritionAPI
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
        emit(Resource.Loading())

        try {
            val nutritionData = api.getNutritionData("087770f5", "d4a5d7cdd11009b7b2fc536d637feb67",ingredient).toNutrition()

            if (nutritionData.foodName == null) {
                emit(Resource.Error("The search has failed. Try looking up something else or switch to custom mode"))
            }
            else { emit(Resource.Success(nutritionData)) }

        } catch (e: HttpException) {
            if (e.code() == 401) {
                emit(Resource.Error("Reached the limit of nutrition API hits. Please contact the developer."))
            }
            else {
                emit(Resource.Error("Something went wrong. Couldn't reach the server. Please try again later."))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Something went wrong. Check your internet connection."))
        }
    }
}