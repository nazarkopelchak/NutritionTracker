package com.example.nutritiontracker.domain.use_case

import com.example.nutritiontracker.data.local.NutritionDatabase
import javax.inject.Inject

class NukeNutritionTable @Inject constructor(
    private val dt: NutritionDatabase
) {
    operator fun invoke() {
        dt.nutritionDao.nukeTable()
    }
}