package com.example.nutritiontracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nutritiontracker.data.local.entity.NutritionEntity
import com.example.nutritiontracker.data.local.entity.RecentNutritionsEntity
import com.example.nutritiontracker.data.local.util.LocalDateConverter

@Database(
    entities = [NutritionEntity::class, RecentNutritionsEntity::class],
    version = 3
)
@TypeConverters(LocalDateConverter::class)
abstract class NutritionDatabase: RoomDatabase() {

    abstract val nutritionDao: NutritionDao
    abstract val recentNutritionDao: RecentNutritionDao
}