package com.nazarkopelchak.nutritiontracker.data.local.util

import androidx.room.TypeConverter
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NutritionConverter {

    val gson = Gson()

    @TypeConverter
    fun stringToListOfNutrition(data: String): List<Nutrition> {
        if (data.isEmpty()) {
            return emptyList()
        }

        return gson.fromJson(data, object : TypeToken<List<Nutrition>>(){}.type)
    }

    @TypeConverter
    fun listOfNutritionToString(list: List<Nutrition>): String {
        return gson.toJson(list)
    }
}