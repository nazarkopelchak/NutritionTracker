package com.example.nutritiontracker.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.domain.model.Meals
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.use_case.GetRemoteNutritionData
import com.example.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.example.nutritiontracker.presentation.util.events.AddNutritionEvent
import com.example.nutritiontracker.presentation.util.AddNutritionState
import com.example.nutritiontracker.presentation.util.AddNutritionTextFields
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.Routes
import com.example.nutritiontracker.utils.capitalized
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

    var meal by mutableStateOf<Meals>(Meals.BREAKFAST)
        private set
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
            is AddNutritionEvent.OnMealChange -> {
                meal = event.meal
            }
            is AddNutritionEvent.OnAmountChange -> {
                _uiState.value = AddNutritionState(
                    customModeOn = _uiState.value.customModeOn,
                    errorTextField = null
                )
                amount = if (event.amount.isBlank() || event.amount.toDoubleOrNull() == 0.0) {
                    previousAmount = ""
                    ""
                } else if (event.amount.toIntOrNull() != null && event.amount.toIntOrNull()!! > 0) {
                    previousAmount = event.amount.toInt().toString()
                    previousAmount
                }
                else if (event.amount.toDoubleOrNull() != null && event.amount.toDoubleOrNull()!! >= 0) {
                    previousAmount = event.amount.toDouble().toString()
                    previousAmount
                } else previousAmount

            }
            AddNutritionEvent.OnButtonClick -> {
                if (foodQuery.isBlank()) {
                    _uiState.value = AddNutritionState(
                        customModeOn = _uiState.value.customModeOn,
                        errorTextField = AddNutritionTextFields.FoodQueryField
                    )
                    sendUiEvent(UiEvent.ShowToast("Ingredient field cannot be empty"))
                    return
                }
                if (_uiState.value.customModeOn) {
                    if (amount.isBlank()) {
                        _uiState.value = AddNutritionState(
                            customModeOn = _uiState.value.customModeOn,
                            errorTextField = AddNutritionTextFields.AmountField
                        )
                        sendUiEvent(UiEvent.ShowToast("Amount field cannot be zero"))
                        return
                    }
                    if (calories.isBlank()) {
                        _uiState.value = AddNutritionState(
                            customModeOn = _uiState.value.customModeOn,
                            errorTextField = AddNutritionTextFields.CaloriesField
                        )
                        sendUiEvent(UiEvent.ShowToast("Calories field cannot be zero"))
                        return
                    }
                    nutrition = Nutrition(
                        meal = meal,
                        foodName = foodQuery.capitalized(),
                        amount = amount.toDouble(),
                        measure = units,
                        calories = calories.toInt(),
                        fat = fat.toDoubleOrNull(),
                        sugar = sugar.toDoubleOrNull(),
                        protein = protein.toDoubleOrNull()
                    )

                    viewModelScope.launch(Dispatchers.Default) {
                        nutritionUseCases.insertLocalNutritionData(nutrition)
                        sendUiEvent(UiEvent.Navigate(Routes.HOME_SCREEN + "?snackBarMessage=Nutrition added"))
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
                                    nutrition = result.data!!.copy(meal = meal)
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
                    sendUiEvent(UiEvent.Navigate(Routes.HOME_SCREEN + "?snackBarMessage=Nutrition added"))
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
                } else if (event.calories.toIntOrNull() != null && event.calories.toIntOrNull()!! >= 0) {
                    previousCalories = event.calories.toInt().toString()
                    previousCalories
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
            is AddNutritionEvent.OnCustomModeClick -> {
                _uiState.value = AddNutritionState(customModeOn = !_uiState.value.customModeOn)
            }
        }
    }

    private fun sendUiEvent(addNutritionEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvents.send(addNutritionEvent)
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->    true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->   true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->   true
                else ->     false
            }
        }
        // For below 29 api
        else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }
}

