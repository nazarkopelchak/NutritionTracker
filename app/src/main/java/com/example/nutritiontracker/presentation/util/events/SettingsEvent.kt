package com.example.nutritiontracker.presentation.util.events

sealed class SettingsEvent {
    data class OnMaxCaloriesChange(val calories: String): SettingsEvent()
    data class OnMaxProteinChange(val protein: String): SettingsEvent()
    data class OnMaxSugarChange(val sugar: String): SettingsEvent()
    data class OnMaxFatChange(val fat: String): SettingsEvent()
    data object OnNutritionResetChange: SettingsEvent()
    data class ResetTime(val time: String): SettingsEvent()
    data object OnSaveButtonClick: SettingsEvent()
    data object OnMaxCaloriesRowClick: SettingsEvent()
    data object OnMaxProteinRowClick: SettingsEvent()
    data object OnMaxSugarRowClick: SettingsEvent()
    data object OnMaxFatRowClick: SettingsEvent()
    data object OnResetTimeRowClick: SettingsEvent()
    data object ClearAllFocus: SettingsEvent()
}