package com.example.nutritiontracker.data.local.util

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun dateToString(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun stringToDate(string: String): LocalDate {
        return LocalDate.parse(string)
    }
}