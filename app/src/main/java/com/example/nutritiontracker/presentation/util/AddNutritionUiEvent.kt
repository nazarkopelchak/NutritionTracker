package com.example.nutritiontracker.presentation.util

sealed class AddNutritionUiEvent {
    data class Navigate(val route: String): AddNutritionUiEvent()
    data class ShowSnackbar(val message: String?, val action: String? = null): AddNutritionUiEvent()
    data class ShowPromptErrorMessage(val message: String): AddNutritionUiEvent()
    data object ShowNutritionDialog: AddNutritionUiEvent()
}