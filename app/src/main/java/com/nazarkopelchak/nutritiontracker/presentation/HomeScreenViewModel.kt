package com.nazarkopelchak.nutritiontracker.presentation

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nazarkopelchak.nutritiontracker.common.Constants
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import com.nazarkopelchak.nutritiontracker.domain.model.TotalNutrition
import com.nazarkopelchak.nutritiontracker.domain.use_case.AddHistoryDataWorker
import com.nazarkopelchak.nutritiontracker.domain.use_case.LocalNutritionUseCases
import com.nazarkopelchak.nutritiontracker.presentation.util.events.HomeScreenEvent
import com.nazarkopelchak.nutritiontracker.presentation.util.events.UiEvent
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.Routes
import com.nazarkopelchak.nutritiontracker.utils.timeDifference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val nutritionUseCases: LocalNutritionUseCases,
    private val sharedPreferences: SharedPreferences,
    private val workManager: WorkManager,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    val listOfNutritions = nutritionUseCases.getNutritionData()

    private val _totalNutrition = MutableStateFlow(TotalNutrition())
    val totalNutrition = _totalNutrition.asStateFlow()

    private val _uiEvents = Channel<UiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    private var deletedNutritionItem: Nutrition? = null

    init {
        viewModelScope.launch(Dispatchers.Default) {
            listOfNutritions.collectLatest {
                _totalNutrition.emit(
                    nutritionUseCases.getTotalNutrition.execute(it)
                )
            }
        }
        val snackBarMessage = savedStateHandle.get<String?>(Constants.ARGUMENT_NAME)
        snackBarMessage?.let { message ->
            if (message != "{${Constants.ARGUMENT_NAME}}") {    // Fixes a bug where it returns the name of the argument
                sendUiEvents(UiEvent.ShowToast(message))
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            val resetTimeEnabled = sharedPreferences.getBoolean(Constants.RESET_TIME_ENABLED, true)

            if (!resetTimeEnabled) {
                workManager.cancelAllWorkByTag(Constants.WORKER_TAG)
            }

            if (resetTimeEnabled && snackBarMessage == "Settings saved") {  //Make sure to only run the work manager after clicking the save button on the settings screen. No point in rescheduling the work manager after each navigation.
                workManager.cancelAllWorkByTag(Constants.WORKER_TAG)
                val timeOffset = timeDifference(sharedPreferences.getString(Constants.RESET_TIME, "0:0") ?: "0:0")
                val minutesOffset = timeOffset.hour * 60 + timeOffset.minute

                val modifyHistoryDataRequest =
                    PeriodicWorkRequestBuilder<AddHistoryDataWorker>(24, TimeUnit.HOURS)
                        .addTag(Constants.WORKER_TAG)
                        .setInitialDelay(minutesOffset.toLong(), TimeUnit.MINUTES)
                        .build()

                workManager.enqueue(modifyHistoryDataRequest)
            }
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
                }
            }
            is HomeScreenEvent.RemoveNutritionItem -> {
                deletedNutritionItem = event.nutrition
                viewModelScope.launch(Dispatchers.Default) {
                    nutritionUseCases.deleteLocalNutritionData(event.nutrition)
                    sendUiEvents(
                        UiEvent.ShowSnackbar(
                        message = "${event.nutrition.foodName} has been removed from the list",
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
