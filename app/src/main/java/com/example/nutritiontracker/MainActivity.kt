package com.example.nutritiontracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nutritiontracker.common.Constants
import com.example.nutritiontracker.presentation.AddNutritionScreen
import com.example.nutritiontracker.presentation.NutritionHistoryScreen
import com.example.nutritiontracker.presentation.NutritionTrackerHomeScreen
import com.example.nutritiontracker.presentation.SettingsScreen
import com.example.nutritiontracker.presentation.util.nav.NavigationDrawerEntries
import com.example.nutritiontracker.presentation.util.nav.NavigationItems
import com.example.nutritiontracker.presentation.util.nav.Routes
import com.example.nutritiontracker.ui.theme.NutritionTrackerTheme
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
                val navItems = remember { NavigationItems.navItems }
                val selectedDrawerSheetIndex = remember { mutableIntStateOf(NavigationDrawerEntries.HomeScreenEntry) }
                val coroutineScope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navController = rememberNavController()

                ModalNavigationDrawer(
                    drawerContent = {
                        ModalDrawerSheet {
                            Spacer(modifier = Modifier.height(16.dp))

                            navItems.forEachIndexed { index, item ->
                                NavigationDrawerItem(
                                    label = {
                                        Text(text = item.title)
                                    },
                                    selected = index == selectedDrawerSheetIndex.intValue,
                                    onClick = {
                                        coroutineScope.launch {
                                            if (item.route != Routes.SETTING_SCREEN) {
                                                selectedDrawerSheetIndex.intValue = index
                                            }
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
                    },
                    drawerState = drawerState
                ) {
                    Scaffold {
                        val initialRun = sharedPreferences.getBoolean(Constants.FIRST_START, true)

                        NavHost(
                            navController = navController,
                            startDestination = if (!initialRun) Routes.HOME_SCREEN else Routes.SETTING_SCREEN
                        ) {
                            composable(
                                route = Routes.HOME_SCREEN + "?snackBarMessage={snackBarMessage}",
                                arguments = listOf(
                                    navArgument(name = "snackBarMessage") {
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    }
                                )
                            ) {
                                NutritionTrackerHomeScreen(
                                    drawerState = drawerState,
                                    onNavigate = { navController.navigate(it.route) { launchSingleTop = true } }
                                )
                            }
                            composable(route = Routes.ADD_NUTRITION_SCREEN) {
                                AddNutritionScreen(
                                    onNavigate = {navController.navigate(it.route){ launchSingleTop = true } },
                                    popBackStack = {
                                        val previousRoute = navController.previousBackStackEntry?.destination?.route
                                        navController.navigate(previousRoute ?: Routes.HOME_SCREEN)
                                    }
                                )
                            }
                            composable(route = Routes.NUTRITION_HISTORY_SCREEN) {
                                NutritionHistoryScreen(
                                    drawerState = drawerState,
                                    onNavigate = { navController.navigate(it.route){ launchSingleTop = true } }
                                )
                            }
                            composable(route = Routes.SETTING_SCREEN) {
                                SettingsScreen(
                                    onNavigate = { navController.navigate(it.route){ launchSingleTop = true } },
                                    popBackStack = {
                                        val previousRoute = navController.previousBackStackEntry?.destination?.route
                                        navController.navigate(previousRoute ?: Routes.HOME_SCREEN)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}