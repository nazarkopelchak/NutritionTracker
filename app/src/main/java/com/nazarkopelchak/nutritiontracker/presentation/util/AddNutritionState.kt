package com.nazarkopelchak.nutritiontracker.presentation.util

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
    data object ProteinField: AddNutritionTextFields()
    data object FatField: AddNutritionTextFields()
    data object SugarField: AddNutritionTextFields()
}
