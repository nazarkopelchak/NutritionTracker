package com.example.nutritiontracker.presentation.util

sealed class AddNutritionEvent {
    data class OnFoodQueryChange(val foodQuary: String): AddNutritionEvent()
    data class OnAmountChange(val amount: Double): AddNutritionEvent()
    data class OnUnitsChange(val units: String): AddNutritionEvent()
    data class OnCaloriesChange(val calories: Int): AddNutritionEvent()
    data class OnFatChange(val fat: Double): AddNutritionEvent()
    data class OnProteinChange(val protein: Double): AddNutritionEvent()
    data class OnSugarChange(val sugar: Double): AddNutritionEvent()
    data object OnCustomModeClick: AddNutritionEvent()
    data object OnButtonClick: AddNutritionEvent()
    data object OnConfirmButtonClick: AddNutritionEvent()
}