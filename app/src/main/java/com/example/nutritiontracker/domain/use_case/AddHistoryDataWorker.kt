package com.example.nutritiontracker.domain.use_case

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.transformWhile

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
                recentNutritionUseCases.insertLocalRecentNutritionData(totalNutrition.toRecentNutrition())
                localNutritionUseCases.nukeTable()
            }
        }
        return Result.success()
    }
}