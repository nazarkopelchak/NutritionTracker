package com.nazarkopelchak.nutritiontracker.domain.use_case

import com.nazarkopelchak.nutritiontracker.data.local.NutritionDatabase
import javax.inject.Inject

class NukeNutritionTable @Inject constructor(
    private val dt: NutritionDatabase
) {
    operator fun invoke() {
        dt.nutritionDao.nukeTable()
    }
}