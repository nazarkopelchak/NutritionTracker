package com.example.nutritiontracker.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.use_case.GetRemoteNutritionData
import com.example.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.example.nutritiontracker.presentation.util.AddNutritionEvent
import com.example.nutritiontracker.presentation.util.AddNutritionUiEvent
import com.example.nutritiontracker.presentation.util.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
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

    var nutrition by mutableStateOf<Nutrition?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var customModeOn by mutableStateOf(false)
        private set
    var foodQuery by mutableStateOf("")
        private set
    var amount by mutableDoubleStateOf(0.0)
        private set
    var units by mutableStateOf("") // Change the type
        private set
    var calories by mutableIntStateOf(0)
        private set
    var fat by mutableDoubleStateOf(0.0)
        private set
    var protein by mutableDoubleStateOf(0.0)
        private set
    var sugar by mutableDoubleStateOf(0.0)
        private set

    private val _uiEvents = Channel<AddNutritionUiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    fun onEvent(event: AddNutritionEvent) {
        when(event) {
            is AddNutritionEvent.OnFoodQueryChange -> { foodQuery = event.foodQuary }
            is AddNutritionEvent.OnAmountChange -> { amount = event.amount }
            AddNutritionEvent.OnButtonClick -> {
                if (foodQuery.isBlank()) {
                    sendUiEvent(AddNutritionUiEvent.ShowPromptErrorMessage("This field cannot be empty."))
                    return
                }
                if (customModeOn) {
                    if (amount <= 0.0) {
                        sendUiEvent(AddNutritionUiEvent.ShowPromptErrorMessage("This field cannot be zero."))
                    }
                    if (calories <= 0) {
                        sendUiEvent(AddNutritionUiEvent.ShowPromptErrorMessage("This field cannot be zero."))
                    }
                    nutrition = Nutrition(
                        foodName = foodQuery,
                        amount = amount,
                        measure = units,
                        calories = calories,
                        fat = fat,
                        sugar = sugar,
                        protein = protein
                    )

                    nutritionUseCases.insertLocalNutritionData(nutrition!!)

                    sendUiEvent(AddNutritionUiEvent.Navigate(Routes.HOME_SCREEN))
                }
                else {
                    viewModelScope.launch {
                        getRemoteNutritionUseCase(foodQuery).collectLatest { result ->
                            when (result) {
                                is Resource.Loading -> {
                                    isLoading = true
                                }
                                is Resource.Error -> {
                                    isLoading = false
                                    sendUiEvent(AddNutritionUiEvent.ShowSnackbar(result.message))
                                }
                                is Resource.Success -> {
                                    isLoading = false
                                    nutrition = result.data
                                    sendUiEvent(AddNutritionUiEvent.ShowNutritionDialog)
                                }
                            }
                        }
                    }
                }
            }
            is AddNutritionEvent.OnConfirmButtonClick -> {
                if (nutrition == null) {
                    sendUiEvent(AddNutritionUiEvent.ShowSnackbar("Something went wrong. Please try again."))
                }
                else {
                    nutritionUseCases.insertLocalNutritionData(nutrition!!)
                }
            }
            is AddNutritionEvent.OnCaloriesChange -> { calories = event.calories }
            is AddNutritionEvent.OnFatChange -> { fat = event.fat }
            is AddNutritionEvent.OnProteinChange -> { protein = event.protein }
            is AddNutritionEvent.OnSugarChange -> { sugar = event.sugar }
            is AddNutritionEvent.OnUnitsChange -> { units = event.units }
            AddNutritionEvent.OnCustomModeClick -> { customModeOn = !customModeOn }
        }
    }

    private fun sendUiEvent(addNutritionEvent: AddNutritionUiEvent) {
        viewModelScope.launch {
            _uiEvents.send(addNutritionEvent)
        }
    }
}

