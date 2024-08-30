package com.example.nutritiontracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.presentation.util.NavigationItems
import com.example.nutritiontracker.presentation.util.RecentNutritionItem
import com.example.nutritiontracker.ui.theme.Shapes
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun NutritionHistoryScreen(
//    onNavigate: (UiEvent.Navigate) -> Unit,
//    viewModel: NutritionHistoryViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val selectedNavigationItem = rememberSaveable { mutableIntStateOf(1) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }
    val filtersClickableText = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        //TODO
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
                        selected = index == selectedNavigationItem.value,
                        onClick = {
                            selectedNavigationItem.value = index
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedNavigationItem.value) { item.selectedIcon }
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
                            text = "Nutrition History"
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
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
            snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        ) { innerPadding ->
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
                                imageVector = if (filtersClickableText.value) Icons.Filled.KeyboardArrowLeft
                                                else Icons.Filled.KeyboardArrowRight,
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
                                selected = true, //FIXME
                                onClick = {
                                    //TODO
                                },
                                label = {
                                    Row(
                                        //modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "Date")
                                        //Spacer(modifier = Modifier.width(8.dp))
                                    }
                                },
                                modifier = Modifier
                                    .padding(4.dp, 0.dp)
                            )
                            FilterChip(
                                selected = false, //FIXME
                                onClick = {
                                    //TODO
                                },
                                label = {
                                    Row(
                                        //modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "Calories")
                                        //Spacer(modifier = Modifier.width(8.dp))
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
                    items(listOfRecentNutritions()) { recentNutrition ->
                        RecentNutritionItem(recentNutrition = recentNutrition)
                    }
                }
            }
        }
    }
}

fun listOfRecentNutritions(): List<RecentNutrition> {
    return listOf(
        RecentNutrition(
            LocalDate.now(),
            1900,
            15.5,
            7.3,
            1.1
        ),
        RecentNutrition(
            LocalDate.of(2012, 12, 12),
            2315,
            24.0,
            11.9,
            4.4
        ),
        RecentNutrition(
            LocalDate.of(1999, 8, 1),
            2500,
            10.0
        )
    )
}