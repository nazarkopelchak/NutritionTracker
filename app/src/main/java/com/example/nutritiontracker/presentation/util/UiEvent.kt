package com.example.nutritiontracker.presentation.util

import com.example.nutritiontracker.domain.model.Nutrition

sealed class UiEvent {
    data object PopBackStack: UiEvent()
    data class Navigate(val route: String): UiEvent()
    data class ShowSnackbar(val message: String, val action: String): UiEvent()
    data class ShowNutritionWindow(val nutrition: Nutrition): UiEvent()
}