package com.example.nutritiontracker.data.local.util

import androidx.room.TypeConverter
import com.example.nutritiontracker.domain.model.Meals

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