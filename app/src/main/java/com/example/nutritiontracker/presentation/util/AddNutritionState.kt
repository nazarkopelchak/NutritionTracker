package com.example.nutritiontracker.presentation.util

data class AddNutritionState(
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val customModeOn: Boolean = false,
    val errorTextField: NutritionTextFields? = null
)

sealed class NutritionTextFields {
    data object FoodQueryField: NutritionTextFields()
    data object AmountField: NutritionTextFields()
    data object CaloriesField: NutritionTextFields()
}
