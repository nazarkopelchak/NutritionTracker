package com.example.nutritiontracker.domain.use_case

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nutritiontracker.domain.model.RecentNutrition
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.transformWhile
import java.time.LocalDate

@HiltWorker
class AddHistoryDataWorker @AssistedInject constructor (
    private val localNutritionUseCases: LocalNutritionUseCases,
    private val recentNutritionUseCases: RecentNutritionUseCases,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
): CoroutineWorker(context, workerParams){

    override suspend fun doWork(): Result {

        val nutritionData = localNutritionUseCases.getNutritionData()
        nutritionData.transformWhile { value ->
            emit(value)
            false
        }.collect { nutrition ->
            if (nutrition.isNotEmpty()) {
                val totalNutrition = localNutritionUseCases.getTotalNutrition.execute(nutritions = nutrition)
                recentNutritionUseCases.insertLocalRecentNutritionData(
                    RecentNutrition(
                        date = LocalDate.now(),
                        listOfNutrition = nutrition,
                        calories = totalNutrition.totalCalories,
                        fat = totalNutrition.totalFat,
                        protein = totalNutrition.totalProtein,
                        sugar = totalNutrition.totalFat
                    ))
                localNutritionUseCases.nukeTable()
            }
        }
        return Result.success()
    }
}