package com.example.nutritiontracker.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.domain.use_case.RecentNutritionUseCases
import com.example.nutritiontracker.presentation.util.FilterChips
import com.example.nutritiontracker.presentation.util.events.NutritionHistoryEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.Routes
import com.example.nutritiontracker.utils.capitalized
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    var recentNutritionFlow = recentNutritionUseCases.getRecentNutritionLocalData()

    private val _recentNutritions = MutableStateFlow(listOf<RecentNutrition>())
    val recentNutritions = _recentNutritions.asStateFlow()

    private val _uiEvents = Channel<UiEvent>()
    val uiEvent = _uiEvents.receiveAsFlow()

    private val _filterChip = mutableStateOf(FilterChips.DATE)
    val filterChips: State<FilterChips> = _filterChip

    private var deletedNutrition: RecentNutrition? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            recentNutritionFlow.collectLatest {
                _recentNutritions.value = it
                sortRecentNutritions()
            }
        }
    }

    fun onEvent(event: NutritionHistoryEvent) {
        when(event) {
            is NutritionHistoryEvent.OnFilterChipClick -> {
                when(event.filterChips) {
                    FilterChips.DATE -> {
                        _filterChip.value = FilterChips.DATE
                        sortRecentNutritions()
                        sendUiEvents(UiEvent.ShowToast("Sorted by date"))
                    }
                    FilterChips.CALORIES -> {
                        _filterChip.value = FilterChips.CALORIES
                        sortRecentNutritions()
                        sendUiEvents(UiEvent.ShowToast("Sorted by calories"))
                    }
                }
            }
            is NutritionHistoryEvent.OnUndoDeleteClick -> {
                val result = viewModelScope.launch(Dispatchers.Default) {
                    deletedNutrition?.let { recentNutritionUseCases.insertLocalRecentNutritionData(it) }
                }
                if (result.isCompleted) { deletedNutrition = null }
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

    private fun sortRecentNutritions() {
        viewModelScope.launch(Dispatchers.Default) {
            when (_filterChip.value) {
                FilterChips.DATE -> {
                    _recentNutritions.value = _recentNutritions.value.sortedBy {
                        it.date
                    }
                }
                FilterChips.CALORIES -> {
                    _recentNutritions.value = _recentNutritions.value.sortedBy {
                        it.calories
                    }
                }
            }
        }
    }

    private fun createRecentNutritionItems() {
        val list = listOf(
            RecentNutrition(
                date = LocalDate.of(2024, 12, 12),
                calories = 1000,
                protein = 65.32,
                fat = 77.99,
                sugar = 11.09
            ),
            RecentNutrition(
                date = LocalDate.of(2024, 11, 12),
                calories = 1500,
                protein = 40.32,
                fat = 30.99,
                sugar = 15.09
            ),
            RecentNutrition(
                date = LocalDate.of(2024, 11, 9),
                calories = 2000,
                protein = 4.32,
                fat = 3.99,
                sugar = 5.09
            ),
            RecentNutrition(
                date = LocalDate.of(2024, 12, 31),
                calories = 3000,
                protein = 41.32,
                fat = 31.99,
                sugar = 51.09
            ),
            RecentNutrition(
                date = LocalDate.of(2023, 12, 31),
                calories = 3000,
                protein = 41.32,
                fat = 31.99,
                sugar = 51.09
            ),
        )
//        viewModelScope.launch(Dispatchers.IO) {
//            for (l in list) {
//                recentNutritionUseCases.insertLocalRecentNutritionData(l)
//            }
//        }
    }
}