package com.example.nutritiontracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
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
import com.example.nutritiontracker.presentation.util.nav.Routes
import com.example.nutritiontracker.ui.theme.NutritionTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = applicationContext.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
        setContent {
            NutritionTrackerTheme {
                Scaffold {
                    val navController = rememberNavController()
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
                                onNavigate = { navController.navigate(it.route) { launchSingleTop = true } }
                            )
                        }
                        composable(route = Routes.ADD_NUTRITION_SCREEN) {
                            AddNutritionScreen(
                                onNavigate = {navController.navigate(it.route){ launchSingleTop = true } },
                                navigateHome = { navController.navigate(Routes.HOME_SCREEN) { launchSingleTop = true} }
                            )
                        }
                        composable(route = Routes.NUTRITION_HISTORY_SCREEN) {
                            NutritionHistoryScreen(
                                onNavigate = { navController.navigate(it.route){ launchSingleTop = true } }
                            )
                        }
                        composable(route = Routes.SETTING_SCREEN) {
                            SettingsScreen(
                                onNavigate = { navController.navigate(it.route){ launchSingleTop = true } },
                                navigateHome = { navController.navigate(Routes.HOME_SCREEN) { launchSingleTop = true} }
                            )
                        }
                    }
                }
            }
        }
    }
}