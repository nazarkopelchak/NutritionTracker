package com.nazarkopelchak.nutritiontracker.domain.use_case

data class RecentNutritionUseCases(
    val getRecentNutritionLocalData: GetRecentNutritionLocalData,
    val insertLocalRecentNutritionData: InsertLocalRecentNutritionData,
    val deleteRecentLocalNutritionData: DeleteRecentLocalNutritionData
)
