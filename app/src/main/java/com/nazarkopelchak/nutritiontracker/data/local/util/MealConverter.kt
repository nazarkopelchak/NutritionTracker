package com.nazarkopelchak.nutritiontracker.data.local.util

import androidx.room.TypeConverter
import com.nazarkopelchak.nutritiontracker.domain.model.Meals

class MealConverter {

    @TypeConverter
    fun stringToMealsEnum(data: String): Meals {
        return Meals.valueOf(data)
    }

    @TypeConverter
    fun mealsEnumToString(meals: Meals): String {
        return meals.toString()
    }
}