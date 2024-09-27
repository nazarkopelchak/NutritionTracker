package com.nazarkopelchak.nutritiontracker.presentation.util.events

sealed class UiEvent {
    data class Navigate(val route: String): UiEvent()
    data class ShowSnackbar(val message: String, val action: String? = null): UiEvent()
    data class ShowToast(val message: String): UiEvent()
    data object PopBackStack: UiEvent()
}