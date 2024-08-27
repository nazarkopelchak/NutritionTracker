package com.example.nutritiontracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutritiontracker.presentation.AddNutritionScreen
import com.example.nutritiontracker.presentation.NutritionTrackerHomeScreen
import com.example.nutritiontracker.presentation.util.Routes
import com.example.nutritiontracker.ui.theme.NutritionTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutritionTrackerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Routes.HOME_SCREEN) {
                    composable(route = Routes.HOME_SCREEN) {
                        NutritionTrackerHomeScreen(
                            onNavigate = { navController.navigate(it.route) }
                        )
                    }
                    composable(route = Routes.ADD_NUTRITION_SCREEN) {
                        AddNutritionScreen(
                            onPopBackStack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}