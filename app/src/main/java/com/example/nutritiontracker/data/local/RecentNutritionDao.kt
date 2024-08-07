package com.example.nutritiontracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nutritiontracker.data.local.entity.RecentNutritionsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentNutritionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNutrition(nutrition: RecentNutritionsEntity)

    @Query("SELECT * FROM recentnutritionsentity")
    fun getNutritions(): Flow<List<RecentNutritionsEntity>>

    @Delete
    fun deleteNutrition(nutrition: RecentNutritionsEntity)
}