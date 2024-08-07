package com.example.nutritiontracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.TotalNutrition
import com.example.nutritiontracker.domain.use_case.NutritionUseCases
import com.example.nutritiontracker.presentation.util.HomeScreenEvent
import com.example.nutritiontracker.presentation.util.Routes
import com.example.nutritiontracker.presentation.util.UiEvent
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
    private val nutritionUseCases: NutritionUseCases,
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
    }

    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            is HomeScreenEvent.OnUndoDeleteClick -> {
                deletedNutritionItem?.let {nutrition ->
                    nutritionUseCases.insertLocalNutritionData(nutrition)
                }
            }
            is HomeScreenEvent.RemoveNutritionItem -> {
                deletedNutritionItem = event.nutrition
                viewModelScope.launch(Dispatchers.Default) {
                    nutritionUseCases.deleteLocalNutritionData(event.nutrition)
                    sendUiEvents(UiEvent.ShowSnackbar(
                        message = "Item has been removed from the list",
                        action = "Undo"
                    ))
                }
            }
            is HomeScreenEvent.OnNutritionItemClick -> {
                sendUiEvents(UiEvent.ShowNutritionWindow(event.nutrition))  // Maybe not needed
            }
            is HomeScreenEvent.OnAddNutritionButtonClick -> {
                sendUiEvents(UiEvent.Navigate(Routes.ADD_NUTRITION_SCREEN))
            }
            is HomeScreenEvent.OnHistoryButtonClick -> {
                sendUiEvents(UiEvent.Navigate(Routes.NUTRITION_HISTORY_SCREEN))
            }
            is HomeScreenEvent.OnSettingsButtonClick -> {
                sendUiEvents(UiEvent.Navigate(Routes.SETTING_SCREEN))
            }
        }
    }

    private fun sendUiEvents(newUiEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvents.send(newUiEvent)
        }
    }

    fun addItems() {
        val n1 = Nutrition("carrot", 25.6, "g", 50, 1.3, 0.5, 0.5)
        val n2 = Nutrition("potato", 20.6, "g", 80, 2.3, 2.5, 0.2)
        val n3 = Nutrition("beetroot", 10.6, "g", 10, 0.3, 0.1, 0.9)

        viewModelScope.launch(Dispatchers.Default) {
            nutritionUseCases.insertLocalNutritionData(n1)
            nutritionUseCases.insertLocalNutritionData(n2)
            nutritionUseCases.insertLocalNutritionData(n3)

        }
    }
}