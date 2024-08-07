package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import javax.inject.Inject

//Developer Only
class ClearLocalData @Inject constructor(
    private val dt: NutritionDatabase
) {
    operator fun invoke() {
        dt.clearAllTables()
    }
}