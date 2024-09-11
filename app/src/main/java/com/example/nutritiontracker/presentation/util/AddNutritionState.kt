package com.example.nutritiontracker.presentation.util

data class AddNutritionState(
    val isLoading: Boolean = false,
    val showDialog: Boolean = false,
    val customModeOn: Boolean = false,
    val errorTextField: AddNutritionTextFields? = null
)

sealed class AddNutritionTextFields {
    data object FoodQueryField: AddNutritionTextFields()
    data object AmountField: AddNutritionTextFields()
    data object CaloriesField: AddNutritionTextFields()
}
