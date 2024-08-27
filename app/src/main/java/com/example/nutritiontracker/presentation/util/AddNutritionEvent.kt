package com.example.nutritiontracker.presentation.util

sealed class AddNutritionEvent {
    data class OnFoodQueryChange(val foodQuary: String): AddNutritionEvent()
    data class OnAmountChange(val amount: String): AddNutritionEvent()
    data class OnUnitsChange(val units: String): AddNutritionEvent()
    data class OnCaloriesChange(val calories: String): AddNutritionEvent()
    data class OnFatChange(val fat: String): AddNutritionEvent()
    data class OnProteinChange(val protein: String): AddNutritionEvent()
    data class OnSugarChange(val sugar: String): AddNutritionEvent()
    data object OnCustomModeClick: AddNutritionEvent()
    data object OnButtonClick: AddNutritionEvent()
    data object OnConfirmButtonClick: AddNutritionEvent()
    data object OnDismissButtonClick: AddNutritionEvent()
}