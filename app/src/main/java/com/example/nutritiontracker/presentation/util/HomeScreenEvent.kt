package com.example.nutritiontracker.presentation.util

import com.example.nutritiontracker.domain.model.Nutrition

sealed class HomeScreenEvent {
    data class RemoveNutritionItem(val nutrition: Nutrition): HomeScreenEvent()
    //data class OnNutritionItemClick(val nutrition: Nutrition): HomeScreenEvent()
    data object OnUndoDeleteClick: HomeScreenEvent()
    data object OnAddNutritionButtonClick: HomeScreenEvent()
    data object OnHistoryButtonClick: HomeScreenEvent()
    data object OnSettingsButtonClick: HomeScreenEvent()
}