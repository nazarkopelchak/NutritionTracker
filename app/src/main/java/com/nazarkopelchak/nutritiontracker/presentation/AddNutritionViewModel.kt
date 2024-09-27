package com.nazarkopelchak.nutritiontracker.presentation

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
import com.nazarkopelchak.nutritiontracker.common.Constants
import com.nazarkopelchak.nutritiontracker.common.Resource
import com.nazarkopelchak.nutritiontracker.domain.model.Meals
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import com.nazarkopelchak.nutritiontracker.domain.use_case.GetRemoteNutritionData
import com.nazarkopelchak.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.nazarkopelchak.nutritiontracker.presentation.util.events.AddNutritionEvent
import com.nazarkopelchak.nutritiontracker.presentation.util.AddNutritionState
import com.nazarkopelchak.nutritiontracker.presentation.util.AddNutritionTextFields
import com.nazarkopelchak.nutritiontracker.presentation.util.events.UiEvent
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.Routes
import com.nazarkopelchak.nutritiontracker.utils.capitalized
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

    var meal by mutableStateOf(Meals.BREAKFAST)
        private set
    var foodQuery by mutableStateOf("")
        private set
    var amount by mutableStateOf("")
        private set
    var units by mutableStateOf(Constants.GRAM_UNITS)
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
    private var previousProtein = ""
    private var previousFat = ""
    private var previousSugar = ""

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
                amount = convertToValidNumericString(event.amount, AddNutritionTextFields.AmountField)

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
                    nutrition = Nutrition(
                        meal = meal,
                        foodName = foodQuery.capitalized(),
                        amount = amount.toDoubleOrNull(),
                        measure = units,
                        calories = calories.toIntOrNull() ?: 0,
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
                fat = convertToValidNumericString(event.fat, AddNutritionTextFields.FatField)
            }
            is AddNutritionEvent.OnProteinChange -> {
                protein = convertToValidNumericString(event.protein, AddNutritionTextFields.ProteinField)
            }
            is AddNutritionEvent.OnSugarChange -> {
                sugar = convertToValidNumericString(event.sugar, AddNutritionTextFields.SugarField)
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

    private fun convertToValidNumericString(value: String, textField: AddNutritionTextFields): String {
        _uiState.value = AddNutritionState(
            customModeOn = _uiState.value.customModeOn,
            errorTextField = null
        )

        var previousValue = ""

        when (textField) {
            AddNutritionTextFields.AmountField -> previousValue = previousAmount
            AddNutritionTextFields.CaloriesField -> previousValue = previousCalories
            AddNutritionTextFields.FatField -> previousValue = previousFat
            AddNutritionTextFields.ProteinField -> previousValue = previousProtein
            AddNutritionTextFields.SugarField -> previousValue = previousSugar
            else -> Unit
        }

        when (textField) {
            AddNutritionTextFields.AmountField,
            AddNutritionTextFields.FatField,
            AddNutritionTextFields.ProteinField,
            AddNutritionTextFields.SugarField -> {
                if (value.isBlank()) {
                    previousValue = ""
                } else if (value.toIntOrNull() != null && value.toInt() >= 0) {
                    previousValue = value
                }
                else if (value.toDoubleOrNull() != null && value.toDouble() >= 0.0) {
                    previousValue = value
                }
            }
            AddNutritionTextFields.CaloriesField -> {
                if (value.isBlank()) {
                    previousValue = ""
                } else if (value.toIntOrNull() != null && value.toInt() >= 0.0) {
                    previousValue = value
                }
            }
            else -> Unit
        }

        when (textField) {
            AddNutritionTextFields.AmountField -> previousAmount = previousValue
            AddNutritionTextFields.CaloriesField -> previousCalories = previousValue
            AddNutritionTextFields.FatField -> previousFat = previousValue
            AddNutritionTextFields.ProteinField -> previousProtein = previousValue
            AddNutritionTextFields.SugarField -> previousSugar = previousValue
            else -> Unit
        }

        return previousValue
    }
}

