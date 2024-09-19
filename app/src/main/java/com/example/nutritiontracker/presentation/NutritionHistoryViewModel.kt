package com.example.nutritiontracker.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.domain.model.Meals
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.domain.use_case.RecentNutritionUseCases
import com.example.nutritiontracker.presentation.util.FilterChips
import com.example.nutritiontracker.presentation.util.events.NutritionHistoryEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.Routes
import com.example.nutritiontracker.utils.capitalized
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NutritionHistoryViewModel @Inject constructor(
    private val recentNutritionUseCases: RecentNutritionUseCases
): ViewModel() {
    val recentNutritionFlow = recentNutritionUseCases.getRecentNutritionLocalData()

    private val _recentNutritions = MutableStateFlow(listOf<RecentNutrition>())
    val recentNutritions = _recentNutritions.asStateFlow()

    val pickedDate = mutableStateOf(LocalDate.now())

    private val _uiEvents = Channel<UiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    private val _filterChip = mutableStateOf(FilterChips.DATE)
    val filterChips: State<FilterChips> = _filterChip

    private val isDescending  = mutableMapOf(
        FilterChips.DATE to false,
        FilterChips.CALORIES to false
    )

    private var deletedNutrition: RecentNutrition? = null
    private var viewModelScopeResult: Job

    init {
        viewModelScopeResult = setRecentNutritions()
    }

    fun onEvent(event: NutritionHistoryEvent) {
        when(event) {
            is NutritionHistoryEvent.OnFilterChipClick -> {
                when(event.filterChips) {
                    FilterChips.DATE -> {
                        _filterChip.value = FilterChips.DATE
                        viewModelScopeResult.cancel()
                        viewModelScopeResult = setRecentNutritions()
                    }
                    FilterChips.CALORIES -> {
                        _filterChip.value = FilterChips.CALORIES
                        viewModelScopeResult.cancel()
                        viewModelScopeResult = setRecentNutritions()
                    }
                    FilterChips.DATE_PICKER -> {
                        _filterChip.value = FilterChips.DATE_PICKER
                        viewModelScopeResult.cancel()
                        viewModelScopeResult = setRecentNutritions()
                    }
                }
            }
            is NutritionHistoryEvent.OnUndoDeleteClick -> {
                viewModelScope.launch(Dispatchers.Default) {
                    deletedNutrition?.let { recentNutritionUseCases.insertLocalRecentNutritionData(it) }
                }
            }
            is NutritionHistoryEvent.RemoveRecentNutritionItem -> {
                deletedNutrition = event.recentNutrition
                viewModelScope.launch(Dispatchers.Default) {
                    recentNutritionUseCases.deleteRecentLocalNutritionData(event.recentNutrition)
                }
                sendUiEvents(
                    UiEvent.ShowSnackbar(
                        message = "${event.recentNutrition.date.dayOfWeek.name.capitalized()}, " +
                                "${event.recentNutrition.date.month.name.capitalized()} " +
                                "${event.recentNutrition.date.dayOfMonth}, ${event.recentNutrition.date.year}" +
                                " nutrition has been removed from the list",
                        action = "Undo"
                    ))

            }
            is NutritionHistoryEvent.OnNavigationItemClick -> {
                if (event.route != Routes.NUTRITION_HISTORY_SCREEN) {
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

    private fun setRecentNutritions(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            recentNutritionFlow.collectLatest {
                _recentNutritions.value = it
                sortRecentNutritions()
            }
        }
    }

    private fun sortRecentNutritions() {
        viewModelScope.launch(Dispatchers.Default) {
            when (_filterChip.value) {
                FilterChips.DATE -> {
                    if (isDescending.getValue(FilterChips.DATE)) {
                        _recentNutritions.value = _recentNutritions.value.sortedByDescending {
                            it.date
                        }
                    }
                    else {
                        _recentNutritions.value = _recentNutritions.value.sortedBy {
                            it.date
                        }
                    }
                    isDescending.replace(FilterChips.DATE, !isDescending.getValue(FilterChips.DATE))
                }
                FilterChips.CALORIES -> {
                    if (isDescending.getValue(FilterChips.CALORIES)) {
                        _recentNutritions.value = _recentNutritions.value.sortedByDescending {
                            it.calories
                        }
                    }
                    else {
                        _recentNutritions.value = _recentNutritions.value.sortedBy {
                            it.calories
                        }
                    }
                    isDescending.replace(FilterChips.CALORIES, !isDescending.getValue(FilterChips.CALORIES))
                }

                FilterChips.DATE_PICKER -> {
                    _recentNutritions.value = _recentNutritions.value.filter {
                        it.date.compareTo(pickedDate.value) == 0
                    }
                    if (_recentNutritions.value.isEmpty()) {
                        sendUiEvents(UiEvent.ShowToast("No nutrition has been found on the selected date"))
                    }
                }
            }
        }
    }

    // For testing purpose
    private fun createRecentNutritionItems() {
        val list = listOf(
            RecentNutrition(
                date = LocalDate.of(2024, 12, 12),
                listOfNutrition = listOf(
                    Nutrition(Meals.LUNCH,"One", 15.0, "g", 500, 3.5, 3.5, 3.5),
                    Nutrition(Meals.DINNER,"Two", 35.0, "g", 1000, 3.5, 3.5, 3.5),
                    Nutrition(Meals.BREAKFAST,"Three", 5.0, "g", 100, 3.5, 3.5, 3.5),
                    Nutrition(Meals.DINNER,"Four", 95.0, "g", 3500, 3.5, 3.5, 3.5)
                ),
                5000,
                12.5,
                15.5,
                5.5
            ),
            RecentNutrition(
                date = LocalDate.of(2024, 11, 12),
                listOfNutrition = listOf(
                    Nutrition(Meals.LUNCH,"two", 35.0, "g", 1000, 3.5, 3.5, 3.5)
                ),
                1000,
                10.0,
                10.0,
                10.0
            ),
            RecentNutrition(
                date = LocalDate.of(2024, 11, 9),
                listOfNutrition = listOf(
                    Nutrition(Meals.BREAKFAST,"three", 5.0, "g", 100, 3.5, 3.5, 3.5)
                )
                ,900,
                5.0,
                5.0,
                5.0
            ),
            RecentNutrition(
                date = LocalDate.of(2024, 12, 31),
                listOfNutrition = listOf(
                    Nutrition(Meals.LUNCH,"four", 95.0, "g", 3500, 3.5, 3.5, 3.5)
                ),
                800,
                8.0,
                8.0,
                4.0
            ),
            RecentNutrition(
                date = LocalDate.of(2023, 12, 31),
                listOfNutrition = listOf(
                    Nutrition(Meals.DINNER,"five", 115.0, "g", 4500, 3.5, 3.5, 3.5)
                )
                ,200,
                1.5,
                0.5,
                0.0
            ),
        )
        viewModelScope.launch(Dispatchers.IO) {
            for (l in list) {
                recentNutritionUseCases.insertLocalRecentNutritionData(l)
            }
        }
    }
}