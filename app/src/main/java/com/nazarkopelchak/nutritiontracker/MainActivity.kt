package com.nazarkopelchak.nutritiontracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nazarkopelchak.nutritiontracker.common.Constants
import com.nazarkopelchak.nutritiontracker.presentation.AddNutritionScreen
import com.nazarkopelchak.nutritiontracker.presentation.NutritionHistoryScreen
import com.nazarkopelchak.nutritiontracker.presentation.NutritionTrackerHomeScreen
import com.nazarkopelchak.nutritiontracker.presentation.SettingsScreen
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.NavigationDrawerEntries
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.NAVIGATION_ITEMS
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.Routes
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.SETTING_NAVIGATION_ITEM
import com.nazarkopelchak.nutritiontracker.ui.theme.NutritionTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = applicationContext.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        setContent {
            NutritionTrackerTheme {
                val initialRun = sharedPreferences.getBoolean(Constants.FIRST_START, true)
                val navItems = remember { NAVIGATION_ITEMS }
                val selectedDrawerSheetIndex = remember { mutableIntStateOf(NavigationDrawerEntries.HOME_SCREEN_ENTRY) }
                val areDrawerGesturesEnabled = remember { mutableStateOf(!initialRun) }
                val coroutineScope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()

                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    navItems.forEachIndexed { index, item ->
                                        NavigationDrawerItem(
                                            label = {
                                                Text(text = item.title)
                                            },
                                            selected = index == selectedDrawerSheetIndex.intValue,
                                            onClick = {
                                                coroutineScope.launch {
                                                    navController.navigate(item.route)
                                                    drawerState.close()
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (index == selectedDrawerSheetIndex.intValue) { item.selectedIcon }
                                                    else { item.unselectedIcon},
                                                    contentDescription = item.title
                                                )
                                            },
                                            modifier = Modifier
                                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                        )
                                    }
                                }
                                Column (
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    NavigationDrawerItem(
                                        label = {
                                            Text(text = SETTING_NAVIGATION_ITEM.title)
                                        },
                                        selected = selectedDrawerSheetIndex.intValue == NavigationDrawerEntries.SETTING_SCREEN_ENTRY,
                                        onClick = {
                                            coroutineScope.launch {
                                                navController.navigate(SETTING_NAVIGATION_ITEM.route)
                                                drawerState.close()
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (NavigationDrawerEntries.SETTING_SCREEN_ENTRY == selectedDrawerSheetIndex.intValue) {
                                                    SETTING_NAVIGATION_ITEM.selectedIcon
                                                }
                                                else { SETTING_NAVIGATION_ITEM.unselectedIcon},
                                                contentDescription = SETTING_NAVIGATION_ITEM.title
                                            )
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }
                            }
                        }
                    },
                    drawerState = drawerState,
                    gesturesEnabled = areDrawerGesturesEnabled.value
                ) {
                    Scaffold {
                        NavHost(
                            navController = navController,
                            startDestination = if (!initialRun) Routes.HOME_SCREEN else Routes.SETTING_SCREEN
                        ) {
                            composable(
                                route = Routes.HOME_SCREEN + "?snackBarMessage={${Constants.ARGUMENT_NAME}}",
                                arguments = listOf(
                                    navArgument(name = Constants.ARGUMENT_NAME) {
                                        defaultValue = null
                                        type = NavType.StringType
                                        nullable = true
                                    }
                                )
                            ) {
                                NutritionTrackerHomeScreen(
                                    drawerState = drawerState,
                                    selectedDrawerItem = selectedDrawerSheetIndex,
                                    drawerGesturesEnabled = areDrawerGesturesEnabled,
                                    onNavigate = { navController.navigate(it.route) { launchSingleTop = true } }
                                )
                            }
                            composable(route = Routes.ADD_NUTRITION_SCREEN) {
                                AddNutritionScreen(
                                    onNavigate = {navController.navigate(it.route){ launchSingleTop = true } },
                                    drawerGesturesEnabled = areDrawerGesturesEnabled,
                                    popBackStack = { popBackStack(navController) }
                                )
                            }
                            composable(route = Routes.NUTRITION_HISTORY_SCREEN) {
                                NutritionHistoryScreen(
                                    drawerState = drawerState,
                                    selectedDrawerItem = selectedDrawerSheetIndex,
                                    drawerGesturesEnabled = areDrawerGesturesEnabled,
                                    onNavigate = { navController.navigate(it.route){ launchSingleTop = true } },
                                    popBackStack = { popBackStack(navController) }
                                )
                            }
                            composable(route = Routes.SETTING_SCREEN) {
                                SettingsScreen(
                                    onNavigate = { navController.navigate(it.route){ launchSingleTop = true } },
                                    drawerGesturesEnabled = areDrawerGesturesEnabled,
                                    popBackStack = { popBackStack(navController) }
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

private fun popBackStack(navController: NavController) {
    val previousRoute = navController.previousBackStackEntry?.destination?.route
    navController.navigate(previousRoute ?: Routes.HOME_SCREEN)
}