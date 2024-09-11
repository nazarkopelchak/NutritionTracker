package com.example.nutritiontracker.presentation.util.events

import com.example.nutritiontracker.domain.model.Nutrition

sealed class HomeScreenEvent {
    data class RemoveNutritionItem(val nutrition: Nutrition): HomeScreenEvent()
    data object OnUndoDeleteClick: HomeScreenEvent()
    data object OnAddNutritionButtonClick: HomeScreenEvent()
    data class OnNavigationItemClick(val route: String): HomeScreenEvent()
}