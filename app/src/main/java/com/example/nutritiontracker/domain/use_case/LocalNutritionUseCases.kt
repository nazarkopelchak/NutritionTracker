package com.example.nutritiontracker.domain.use_case

data class LocalNutritionUseCases(
    val getNutritionData: GetNutritionLocalData,
    val insertLocalNutritionData: InsertLocalNutritionData,
    val deleteLocalNutritionData: DeleteLocalNutritionData,
    val getTotalNutrition: GetTotalNutrition,
    val clearAllLocalData: ClearLocalData   // Developer Only
)
