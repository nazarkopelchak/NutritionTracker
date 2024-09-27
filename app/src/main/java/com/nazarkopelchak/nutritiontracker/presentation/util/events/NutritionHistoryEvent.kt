package com.nazarkopelchak.nutritiontracker.presentation.util.events

import com.nazarkopelchak.nutritiontracker.domain.model.RecentNutrition
import com.nazarkopelchak.nutritiontracker.presentation.util.FilterChips

sealed class NutritionHistoryEvent {
    data class RemoveRecentNutritionItem(val recentNutrition: RecentNutrition): NutritionHistoryEvent()
    data object OnUndoDeleteClick: NutritionHistoryEvent()
    data class OnFilterChipClick(val filterChips: FilterChips): NutritionHistoryEvent()
    data class OnNavigationItemClick(val route: String): NutritionHistoryEvent()
}
