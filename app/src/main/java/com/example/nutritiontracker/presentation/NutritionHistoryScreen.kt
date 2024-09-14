package com.example.nutritiontracker.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutritiontracker.R
import com.example.nutritiontracker.presentation.util.FilterChips
import com.example.nutritiontracker.presentation.util.RecentNutritionItem
import com.example.nutritiontracker.presentation.util.events.NutritionHistoryEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.NavigationDrawerEntries
import com.example.nutritiontracker.presentation.util.nav.NavigationItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionHistoryScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: NutritionHistoryViewModel = hiltViewModel()
) {
    val recentNutritions = viewModel.recentNutritions.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val selectedNavigationItem = remember { mutableIntStateOf(NavigationDrawerEntries.HistoryScreenEntry) }
    val drawerCoroutineScope = rememberCoroutineScope()
    val filterCoroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val filtersClickableText = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val animVisibleState = remember { MutableTransitionState(false) }
        .apply { targetState = true }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest {event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarState.showSnackbar(event.message, event.action, duration = SnackbarDuration.Short)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(NutritionHistoryEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                NavigationItems.navItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = item.title)
                        },
                        selected = index == selectedNavigationItem.intValue,
                        onClick = {
                            viewModel.onEvent(NutritionHistoryEvent.OnNavigationItemClick(item.route))
                            //selectedNavigationItem.value = index
                            drawerCoroutineScope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedNavigationItem.intValue) { item.selectedIcon }
                                else { item.unselectedIcon},
                                contentDescription = item.title
                            )
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Nutrition History",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 24.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                drawerCoroutineScope.launch {
                                    drawerState.open()
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    })
            },
            snackbarHost = { SnackbarHost(hostState = snackbarState) },
        ) { innerPadding ->
            if (recentNutritions.value.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_icon2),
                        contentDescription = "<a href=\"https://www.flaticon.com/free-icons/empty\" title=\"empty icons\">Empty icons created by Freepik - Flaticon</a>"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "There's nothing here yet.",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        FilterChip(
                            selected = filtersClickableText.value,
                            onClick = { filtersClickableText.value = !filtersClickableText.value },
                            label = { Text(text = "Filter") },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (filtersClickableText.value) Icons.AutoMirrored.Filled.KeyboardArrowLeft
                                    else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Suggestion arrow"
                                )
                            },
                            modifier = Modifier.padding(4.dp, 0.dp)
                        )
                        AnimatedVisibility(visible = filtersClickableText.value) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilterChip(
                                    selected = viewModel.filterChips.value == FilterChips.DATE,
                                    onClick = {
                                        animVisibleState.targetState = false
                                        filterCoroutineScope.launch {
                                            delay(300)
                                            viewModel.onEvent(NutritionHistoryEvent.OnFilterChipClick(FilterChips.DATE))
                                            animVisibleState.targetState = true
                                        }
                                    },
                                    label = {
                                        Row(
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "Date")
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(4.dp, 0.dp)
                                )
                                FilterChip(
                                    selected = viewModel.filterChips.value == FilterChips.CALORIES,
                                    onClick = {
                                        animVisibleState.targetState = false
                                        filterCoroutineScope.launch {
                                            delay(300)
                                            viewModel.onEvent(NutritionHistoryEvent.OnFilterChipClick(FilterChips.CALORIES))
                                            animVisibleState.targetState = true
                                        }
                                    },
                                    label = {
                                        Row(
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "Calories")
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(4.dp, 0.dp)
                                )
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(2.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(recentNutritions.value) { recentNutrition ->
                            AnimatedVisibility(
                                visibleState = animVisibleState
                            ) {
                                RecentNutritionItem(recentNutrition = recentNutrition, viewModel::onEvent)
                            }
                        }
                    }
                }
            }
        }
    }
}