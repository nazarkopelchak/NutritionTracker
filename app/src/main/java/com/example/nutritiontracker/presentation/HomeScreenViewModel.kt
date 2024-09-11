package com.example.nutritiontracker.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.TotalNutrition
import com.example.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.example.nutritiontracker.presentation.util.events.HomeScreenEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val nutritionUseCases: LocalNutritionUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val listOfNutritions = nutritionUseCases.getNutritionData()

    private val _totalNutrition = MutableStateFlow(TotalNutrition())
    val totalNutrition = _totalNutrition.asStateFlow()

    private val _uiEvents = Channel<UiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    private var deletedNutritionItem: Nutrition? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listOfNutritions.collectLatest {
                _totalNutrition.emit(
                    nutritionUseCases.getTotalNutrition.execute(it)
                )
            }
        }
        val snackBarMessage = savedStateHandle.get<String?>("snackBarMessage")
        snackBarMessage?.let { message ->
            sendUiEvents(UiEvent.ShowToast(message))
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            is HomeScreenEvent.OnUndoDeleteClick -> {
                deletedNutritionItem?.let {deletedNutrition ->
                    val scopeResult = viewModelScope.launch(Dispatchers.Default) {
                        nutritionUseCases.insertLocalNutritionData(deletedNutrition)
                    }
                    if (scopeResult.isCompleted) { deletedNutritionItem = null }
                    //deletedNutritionItem = null
                }
            }
            is HomeScreenEvent.RemoveNutritionItem -> {
                deletedNutritionItem = event.nutrition
                viewModelScope.launch(Dispatchers.Default) {
                    nutritionUseCases.deleteLocalNutritionData(event.nutrition)
                    sendUiEvents(
                        UiEvent.ShowSnackbar(
                        message = "Item has been removed from the list",
                        action = "Undo"
                    ))
                }
            }
            is HomeScreenEvent.OnAddNutritionButtonClick -> {
                sendUiEvents(UiEvent.Navigate(Routes.ADD_NUTRITION_SCREEN))
            }

            is HomeScreenEvent.OnNavigationItemClick -> {
                if (event.route != Routes.HOME_SCREEN) {
                    sendUiEvents(UiEvent.Navigate(event.route))
                }
            }
        }
    }

    private fun sendUiEvents(newUiEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvents.send(newUiEvent)
        }
    }
}