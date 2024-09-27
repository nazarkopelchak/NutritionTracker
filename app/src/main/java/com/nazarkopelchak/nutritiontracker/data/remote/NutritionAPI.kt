package com.nazarkopelchak.nutritiontracker.data.remote

import com.nazarkopelchak.nutritiontracker.data.remote.dto.NutritionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionAPI {

    @GET("/api/nutrition-data")
    suspend fun getNutritionData(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") ingredient: String
    ) : NutritionDto
}