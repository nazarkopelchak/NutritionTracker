package com.example.nutritiontracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import com.example.nutritiontracker.common.Constants
import com.example.nutritiontracker.common.Resource
import com.example.nutritiontracker.data.local.NutritionDatabase
import com.example.nutritiontracker.data.local.entity.NutritionEntity
import com.example.nutritiontracker.data.remote.NutritionAPI
import com.example.nutritiontracker.data.remote.dto.toNutrition
import com.example.nutritiontracker.data.reposiitory.NutritionRepositoryImpl
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.domain.repository.NutritionRepository
import com.example.nutritiontracker.domain.use_case.GetTotalNutrition
import com.example.nutritiontracker.presentation.HomeScreenViewModel
import com.example.nutritiontracker.presentation.NutritionItem
import com.example.nutritiontracker.presentation.util.HomeScreenEvent
import com.example.nutritiontracker.ui.theme.NutritionTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            NutritionTrackerTheme {
                val viewModel: HomeScreenViewModel = hiltViewModel()
                val nutr = viewModel.totalNutrition.collectAsState()
                val listOfNutrs = viewModel.listOfNutritions.collectAsState(initial = emptyList())

//                LaunchedEffect(key1 = true) {
//                    viewModel.addItems()
//                }


                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(0.dp, 32.dp)
                    ) {
                        items(listOfNutrs.value) { nutrition ->
                            NutritionItem(
                                nutrition = nutrition
                                //onEvent = viewModel::onEvent
                            )
                        }
                    }
                    val d1 = nutr.value.totalFat?.let { BigDecimal(it).setScale(1, RoundingMode.HALF_EVEN) }
                    val d2 = nutr.value.totalSugar?.let { BigDecimal(it).setScale(1, RoundingMode.HALF_EVEN) }
                    val d3 = nutr.value.totalProtein?.let { BigDecimal(it).setScale(1, RoundingMode.HALF_EVEN) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = nutr.value.totalCalories.toString())
                    Text(text = d1.toString())
                    Text(text = d2.toString())
                    Text(text = d3.toString())
                }


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NutritionTrackerTheme {
        Greeting("Android")
    }
}