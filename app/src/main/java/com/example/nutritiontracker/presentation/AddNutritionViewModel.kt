package com.example.nutritiontracker.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.use_case.GetRemoteNutritionData
import com.example.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.example.nutritiontracker.presentation.util.AddNutritionEvent
import com.example.nutritiontracker.presentation.util.AddNutritionState
import com.example.nutritiontracker.presentation.util.NutritionTextFields
import com.example.nutritiontracker.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNutritionViewModel @Inject constructor(
  private val nutritionUseCases: LocalNutritionUseCases,
  private val getRemoteNutritionUseCase: GetRemoteNutritionData
): ViewModel() {
    var nutrition by mutableStateOf(Nutrition())
        private set

    private val _uiState = mutableStateOf(AddNutritionState())
    val uiState: State<AddNutritionState> = _uiState

    var foodQuery by mutableStateOf("")
        private set
    var amount by mutableStateOf("")
        private set
    var units by mutableStateOf("g") // Change the type
        private set
    var calories by mutableStateOf("")
        private set
    var fat by mutableStateOf("")
        private set
    var protein by mutableStateOf("")
        private set
    var sugar by mutableStateOf("")
        private set

    private val _uiEvents = Channel<UiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    private var previousAmount = ""
    private var previousCalories = ""

    fun onEvent(event: AddNutritionEvent) {
        when(event) {
            is AddNutritionEvent.OnFoodQueryChange -> {
                _uiState.value = AddNutritionState(
                    customModeOn = _uiState.value.customModeOn,
                    errorTextField = null
                )
                foodQuery = event.foodQuary
            }
            is AddNutritionEvent.OnAmountChange -> {
                _uiState.value = AddNutritionState(
                    customModeOn = _uiState.value.customModeOn,
                    errorTextField = null
                )
                amount = if (event.amount.isBlank()) {
                    previousAmount = ""
                    ""
                } else if (event.amount.toDoubleOrNull() != null) {
                    previousAmount = event.amount
                    event.amount
                } else previousAmount

            }
            AddNutritionEvent.OnButtonClick -> {
                if (foodQuery.isBlank()) {
                    _uiState.value = AddNutritionState(
                        customModeOn = _uiState.value.customModeOn,
                        errorTextField = NutritionTextFields.FoodQueryField
                    )
                    sendUiEvent(UiEvent.ShowSnackbar("Ingredient field cannot be empty."))
                    return
                }
                if (_uiState.value.customModeOn) {
                    if (amount.isBlank()) {
                        _uiState.value = AddNutritionState(
                            customModeOn = _uiState.value.customModeOn,
                            errorTextField = NutritionTextFields.AmountField
                        )
                        sendUiEvent(UiEvent.ShowSnackbar("Amount field cannot be zero."))
                        return
                    }
                    if (calories.isBlank()) {
                        _uiState.value = AddNutritionState(
                            customModeOn = _uiState.value.customModeOn,
                            errorTextField = NutritionTextFields.CaloriesField
                        )
                        sendUiEvent(UiEvent.ShowSnackbar("Calories field cannot be zero."))
                        return
                    }
                    nutrition = Nutrition(
                        foodName = foodQuery,
                        amount = amount.toDouble(),
                        measure = units,
                        calories = calories.toInt(),
                        fat = fat.toDoubleOrNull(),
                        sugar = sugar.toDoubleOrNull(),
                        protein = protein.toDoubleOrNull()
                    )

                    viewModelScope.launch(Dispatchers.Default) {
                        nutritionUseCases.insertLocalNutritionData(nutrition)
                        sendUiEvent(UiEvent.PopBackStack)
                    }
                }
                else {
                    viewModelScope.launch {
                        getRemoteNutritionUseCase(foodQuery).collectLatest { result ->
                            when (result) {
                                is Resource.Loading -> {
                                    _uiState.value = AddNutritionState(isLoading = true)
                                }
                                is Resource.Error -> {
                                    _uiState.value = AddNutritionState(isLoading = false)
                                    sendUiEvent(UiEvent.ShowSnackbar(result.message ?: "Null Exception"))
                                }
                                is Resource.Success -> {
                                    nutrition = result.data!!
                                    _uiState.value = AddNutritionState(isLoading = false, showDialog = true)
                                }
                            }
                        }
                    }
                }
            }
            is AddNutritionEvent.OnConfirmButtonClick -> {
                val result = viewModelScope.launch(Dispatchers.Default) {
                    nutritionUseCases.insertLocalNutritionData(nutrition)
                }
                result.invokeOnCompletion {
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
            is AddNutritionEvent.OnDismissButtonClick -> {
                _uiState.value = AddNutritionState(showDialog = false)
            }
            is AddNutritionEvent.OnCaloriesChange -> {
                _uiState.value = AddNutritionState(
                    customModeOn = _uiState.value.customModeOn,
                    errorTextField = null
                )
                calories = if (event.calories.isBlank()) {
                    previousCalories = ""
                    ""
                } else if (event.calories.toIntOrNull() != null) {
                    previousCalories = event.calories
                    event.calories
                }
                else previousCalories

            }
            is AddNutritionEvent.OnFatChange -> {
                fat = event.fat
            }
            is AddNutritionEvent.OnProteinChange -> {
                protein = event.protein
            }
            is AddNutritionEvent.OnSugarChange -> {
                sugar = event.sugar
            }
            is AddNutritionEvent.OnUnitsChange -> { units = event.units }
            AddNutritionEvent.OnCustomModeClick -> {
                _uiState.value = AddNutritionState(customModeOn = !_uiState.value.customModeOn)
            }
        }
    }

    private fun sendUiEvent(addNutritionEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvents.send(addNutritionEvent)
        }
    }
}

