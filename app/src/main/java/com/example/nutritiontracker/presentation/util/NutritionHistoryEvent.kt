package com.example.nutritiontracker.presentation.util

import com.example.nutritiontracker.domain.model.RecentNutrition

sealed class NutritionHistoryEvent {
    data class RemoveRecentNutritionItem(val recentNutrition: RecentNutrition): NutritionHistoryEvent()
    data object OnUndoDeleteClick: NutritionHistoryEvent()
    data class OnFilterChipClick(val filterChips: FilterChips): NutritionHistoryEvent()
}
