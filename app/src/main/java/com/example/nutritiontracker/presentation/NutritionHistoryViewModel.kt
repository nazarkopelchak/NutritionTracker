package com.example.nutritiontracker.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.domain.use_case.RecentNutritionUseCases
import com.example.nutritiontracker.presentation.util.FilterChips
import com.example.nutritiontracker.presentation.util.NutritionHistoryEvent
import com.example.nutritiontracker.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionHistoryViewModel @Inject constructor(
    private val recentNutritionUseCases: RecentNutritionUseCases
): ViewModel() {
    val recentNutritions = recentNutritionUseCases.getRecentNutritionLocalData()     //TODO sort the recent nutrition list

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _filterChip = mutableStateOf(FilterChips.DATE)
    val filterChips: State<FilterChips> = _filterChip

    private var deletedNutrition: RecentNutrition? = null
    //private var deletedIndex: Int? = null

    fun onEvent(event: NutritionHistoryEvent) {
        when(event) {
            is NutritionHistoryEvent.OnFilterChipClick -> {
                if (event.filterChips == FilterChips.CALORIES) { _filterChip.value = FilterChips.CALORIES }     //TODO sort the recent nutrition list
                else { _filterChip.value = FilterChips.DATE }
            }
            is NutritionHistoryEvent.OnUndoDeleteClick -> {
                val result = viewModelScope.launch(Dispatchers.Default) {
                    deletedNutrition?.let { recentNutritionUseCases.insertLocalRecentNutritionData(it) }     //TODO sort the recent nutrition list
                }
                if (result.isCompleted) { deletedNutrition = null }
            }
            is NutritionHistoryEvent.RemoveRecentNutritionItem -> {
                val result = viewModelScope.launch(Dispatchers.Default) {
                    recentNutritionUseCases.deleteRecentLocalNutritionData(event.recentNutrition)
                }
                deletedNutrition = event.recentNutrition
                if (result.isCompleted) {
                    sendUiEvents(UiEvent.ShowSnackbar(
                        message = "Item has been removed from the list",
                        action = "Undo"
                    ))
                }
                else {
                    sendUiEvents(UiEvent.ShowSnackbar(
                        message = "Something went wrong. Please try again."
                    ))
                }

            }
        }
    }

    private fun sendUiEvents(newUiEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(newUiEvent)
        }
    }
}