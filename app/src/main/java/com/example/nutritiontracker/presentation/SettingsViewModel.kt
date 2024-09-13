package com.example.nutritiontracker.presentation

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.common.Constants
import com.example.nutritiontracker.presentation.util.SettingsTextFieldsState
import com.example.nutritiontracker.presentation.util.events.SettingsEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val sharedPreferences: SharedPreferences
): ViewModel() {

    var isFirstTimeRun by mutableStateOf(true)
        private set

    var maxCalories by mutableStateOf("")
        private set

    var maxProtein by mutableStateOf("")
        private set

    var maxSugar by mutableStateOf("")
        private set

    var maxFat by mutableStateOf("")
        private set

    var enableNutritionReset by mutableStateOf(true)
        private set

    var resetTime by mutableStateOf("")
        private set

    private val _textFieldState = mutableStateOf(SettingsTextFieldsState())
    val textFieldState: State<SettingsTextFieldsState> = _textFieldState

    private val _uiEvents = Channel<UiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    private var previousValue = ""
    private var militaryTime : String

    init {
        isFirstTimeRun = sharedPreferences.getBoolean(Constants.FIRST_START, true)
        maxCalories = sharedPreferences.getString(Constants.MAX_CALORIES, "2000") ?: "ERROR"
        maxProtein = sharedPreferences.getString(Constants.MAX_PROTEIN, "60") ?: "ERROR"
        maxSugar = sharedPreferences.getString(Constants.MAX_SUGAR, "30") ?: "ERROR"
        maxFat = sharedPreferences.getString(Constants.MAX_FAT, "60") ?: "ERROR"
        enableNutritionReset = sharedPreferences.getBoolean(Constants.RESET_TIME_ENABLED, true)
        resetTime = militaryToRegularTime(sharedPreferences.getString(Constants.RESET_TIME, "12:00 AM") ?: "ERROR")
        militaryTime = sharedPreferences.getString(Constants.RESET_TIME, "0:0") ?: "ERROR"
    }

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.OnMaxCaloriesChange -> {
                _textFieldState.value = SettingsTextFieldsState(caloriesVisibility = true)
                maxCalories = numberToStringVerification(event.calories)
            }
            is SettingsEvent.OnMaxFatChange -> {
                _textFieldState.value = SettingsTextFieldsState(fatVisibility = true)
                maxFat = numberToStringVerification(event.fat)
            }
            is SettingsEvent.OnMaxProteinChange -> {
                _textFieldState.value = SettingsTextFieldsState(proteinVisibility = true)
                maxProtein = numberToStringVerification(event.protein)
            }
            is SettingsEvent.OnMaxSugarChange -> {
                _textFieldState.value = SettingsTextFieldsState(sugarVisibility = true)
                maxSugar = numberToStringVerification(event.sugar)
            }
            is SettingsEvent.OnNutritionResetChange -> {
                _textFieldState.value = SettingsTextFieldsState()
                enableNutritionReset = !enableNutritionReset
            }
            is SettingsEvent.ResetTime -> {
                militaryTime = event.time
                resetTime = militaryToRegularTime(event.time)
            }
            is SettingsEvent.OnSaveButtonClick -> {
                if (maxCalories.isBlank()) maxCalories = "0"
                if (maxProtein.isBlank()) maxProtein = "0"
                if (maxSugar.isBlank()) maxSugar = "0"
                if (maxFat.isBlank()) maxFat = "0"

                val editor = sharedPreferences.edit()

                editor.putBoolean(Constants.FIRST_START, false)

                editor.putString(Constants.MAX_CALORIES, maxCalories)
                editor.putString(Constants.MAX_PROTEIN, maxProtein)
                editor.putString(Constants.MAX_SUGAR, maxSugar)
                editor.putString(Constants.MAX_FAT, maxFat)

                editor.putBoolean(Constants.RESET_TIME_ENABLED, enableNutritionReset)
                editor.putString(Constants.RESET_TIME, militaryTime)
                editor.apply()

                sendUiEvent(UiEvent.Navigate(Routes.HOME_SCREEN + "?snackBarMessage=Settings saved"))
            }

            SettingsEvent.OnMaxCaloriesRowClick -> {
                if (maxCalories.isBlank()) { maxCalories = "0" }
                previousValue = maxCalories
                _textFieldState.value = SettingsTextFieldsState(caloriesVisibility = !_textFieldState.value.caloriesVisibility)
            }
            SettingsEvent.OnMaxProteinRowClick -> {
                if (maxProtein.isBlank()) { maxProtein = "0" }
                previousValue = maxProtein
                _textFieldState.value = SettingsTextFieldsState(proteinVisibility = !_textFieldState.value.proteinVisibility)
            }
            SettingsEvent.OnMaxFatRowClick -> {
                if (maxFat.isBlank()) { maxFat = "0" }
                previousValue = maxFat
                _textFieldState.value = SettingsTextFieldsState(fatVisibility = !_textFieldState.value.fatVisibility)
            }
            SettingsEvent.OnMaxSugarRowClick -> {
                if (maxSugar.isBlank()) { maxSugar = "0" }
                previousValue = maxSugar
                _textFieldState.value = SettingsTextFieldsState(sugarVisibility = !_textFieldState.value.sugarVisibility)
            }
            SettingsEvent.OnResetTimeRowClick -> {
                _textFieldState.value = SettingsTextFieldsState(resetTimeVisibility = !_textFieldState.value.resetTimeVisibility)
            }

            SettingsEvent.ClearAllFocus -> _textFieldState.value = SettingsTextFieldsState()
        }
    }

    private fun numberToStringVerification(string: String): String {
        if (string.isBlank()) {
            previousValue = ""
            return ""
        }

        return if (string.toIntOrNull() != null && string.toIntOrNull()!! >= 0) {   // if null, second statement will not be reached
                previousValue = string.toInt().toString()   // Transformation is required to stop values like "02.02" from occurring.
                string.toInt().toString()
            } else previousValue
        }

    private fun militaryToRegularTime(milTime: String): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = timeFormat.parse(milTime)

        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date!!)
    }

    private fun sendUiEvent(settingUiEvent: UiEvent) {
        viewModelScope.launch {
            _uiEvents.send(settingUiEvent)
        }
    }
}