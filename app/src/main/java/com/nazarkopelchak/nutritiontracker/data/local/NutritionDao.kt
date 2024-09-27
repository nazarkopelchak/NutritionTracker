package com.nazarkopelchak.nutritiontracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nazarkopelchak.nutritiontracker.data.local.entity.NutritionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNutrition(nutrition: NutritionEntity)

    @Query("SELECT * FROM nutritionentity")
    fun getNutritions(): Flow<List<NutritionEntity>>

    @Delete
    fun deleteNutrition(nutrition: NutritionEntity)

    @Query("DELETE FROM nutritionentity")
    fun nukeTable()
}