package com.example.nutritiontracker.presentation.util

sealed class HomeScreemUiEvent {
    data class Navigate(val route: String): HomeScreemUiEvent()
    data class ShowSnackbar(val message: String, val action: String): HomeScreemUiEvent()
}