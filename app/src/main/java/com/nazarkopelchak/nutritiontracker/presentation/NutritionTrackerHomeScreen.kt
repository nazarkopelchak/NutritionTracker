package com.nazarkopelchak.nutritiontracker.presentation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nazarkopelchak.nutritiontracker.R
import com.nazarkopelchak.nutritiontracker.common.Constants
import com.nazarkopelchak.nutritiontracker.domain.model.Meals
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import com.nazarkopelchak.nutritiontracker.presentation.util.CircularProgressBar
import com.nazarkopelchak.nutritiontracker.presentation.util.NutritionItem
import com.nazarkopelchak.nutritiontracker.presentation.util.events.HomeScreenEvent
import com.nazarkopelchak.nutritiontracker.presentation.util.events.UiEvent
import com.nazarkopelchak.nutritiontracker.presentation.util.nav.NavigationDrawerEntries
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionTrackerHomeScreen(
    drawerState: DrawerState,
    selectedDrawerItem: MutableIntState,
    drawerGesturesEnabled: MutableState<Boolean>,
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val nutritions = viewModel.listOfNutritions.collectAsState(initial = emptyList())
    val totalNutrition = viewModel.totalNutrition.collectAsState()
    val snackbarState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val activity = LocalContext.current as? Activity
    val sharedPreferences = activity?.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
    val animVisibleState = remember { MutableTransitionState(false) }
        .apply { targetState = true }

    selectedDrawerItem.intValue = NavigationDrawerEntries.HOME_SCREEN_ENTRY  // Make sure the drawer item is always correctly selected
    drawerGesturesEnabled.value = true

    BackHandler {
        activity?.finish()
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest {event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarState.showSnackbar(event.message, event.action, duration = SnackbarDuration.Short)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HomeScreenEvent.OnUndoDeleteClick)
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                is UiEvent.ShowToast -> {
                    Toast.makeText(activity, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
             TopAppBar(title = {
                 Row(
                     horizontalArrangement = Arrangement.SpaceBetween,
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth()
                 ) {
                     Text(
                         style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.sp),
                         text = "NutritionTracker",
                         color = MaterialTheme.colorScheme.primary
                     )
                     Image(
                         painter = painterResource(id = R.drawable.nutrition_tracker_icon),
                         contentDescription = null,
                         modifier = Modifier.size(48.dp)
                     )
                 }
             },
                 navigationIcon = {
                     IconButton(
                         onClick = {
                             coroutineScope.launch {
                                 drawerState.open()
                             }
                         }
                     ) {
                         Icon(
                             imageVector = Icons.Default.Menu,
                             contentDescription = "Menu"
                         )
                     }
                 }
             )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarState)},
        floatingActionButton = {
            FloatingActionButton( onClick = {
                viewModel.onEvent(HomeScreenEvent.OnAddNutritionButtonClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        modifier = Modifier.fillMaxSize()) {innerPadding ->

        // Display this image if there is no data to display
        if (nutritions.value.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.empty_icon),
                    contentDescription = "<a href=\"https://www.flaticon.com/free-icons/empty\" title=\"empty icons\">Empty icons created by Leremy - Flaticon</a>"
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "There seems to be nothing here",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                        CircularProgressBar(
                            percentage = if (sharedPreferences?.getString(Constants.MAX_CALORIES, "2000") != "0") {
                                    totalNutrition.value.totalCalories.toFloat() / sharedPreferences?.getString(Constants.MAX_CALORIES, "2000")!!.toFloat() }
                                else { totalNutrition.value.totalCalories.toFloat() },
                            maxNumber = sharedPreferences.getString(Constants.MAX_CALORIES, "2000")!!.toInt(),
                            color = Color(android.graphics.Color.parseColor("#00b300")),
                            title = "Calories",
                            radius = 80.dp
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressBar(
                                percentage = if (sharedPreferences?.getString(Constants.MAX_PROTEIN, "60") != "0") {
                                        totalNutrition.value.totalProtein.toFloat() / sharedPreferences?.getString(Constants.MAX_PROTEIN, "60")!!.toFloat() }
                                    else { totalNutrition.value.totalProtein.toFloat() },
                                maxNumber = sharedPreferences.getString(Constants.MAX_PROTEIN, "60")!!.toInt(),
                                color = Color(android.graphics.Color.parseColor("#ffcc80")),
                                title = "Protein",
                                convertToInt = false,
                                fontSize = 18.sp,
                                radius = 45.dp
                            )
                        }
                        Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressBar(
                                percentage = if (sharedPreferences?.getString(Constants.MAX_SUGAR, "30") != "0") {
                                        totalNutrition.value.totalSugar.toFloat() / sharedPreferences?.getString(Constants.MAX_SUGAR, "30")!!.toFloat() }
                                    else { totalNutrition.value.totalSugar.toFloat() },
                                maxNumber = sharedPreferences.getString(Constants.MAX_SUGAR, "30")!!.toInt(),
                                color = Color(android.graphics.Color.parseColor("#c61aff")),
                                title = "Sugar",
                                convertToInt = false,
                                fontSize = 18.sp,
                                radius = 45.dp
                            )
                        }
                        Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressBar(
                                percentage = if (sharedPreferences?.getString(Constants.MAX_FAT, "60") != "0") {
                                        totalNutrition.value.totalFat.toFloat() / sharedPreferences?.getString(Constants.MAX_FAT, "60")!!.toFloat() }
                                    else { totalNutrition.value.totalFat.toFloat() },
                                maxNumber = sharedPreferences.getString(Constants.MAX_FAT, "60")!!.toInt(),
                                color = Color(android.graphics.Color.parseColor("#99ffdd")),    // Dark cyan = #002633     Light cyan = #66d9ff
                                title = "Fat",
                                convertToInt = false,
                                fontSize = 18.sp,
                                radius = 45.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(
                    visibleState = animVisibleState,
                    enter = slideInHorizontally()
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        NutritionCard(
                            "Breakfast",
                            nutritions.value.filter { it.meal == Meals.BREAKFAST },
                            viewModel::onEvent
                        )
                        NutritionCard(
                            "Lunch",
                            nutritions.value.filter { it.meal == Meals.LUNCH },
                            viewModel::onEvent
                        )
                        NutritionCard(
                            "Dinner",
                            nutritions.value.filter { it.meal == Meals.DINNER },
                            viewModel::onEvent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun NutritionCard(
    meal: String,
    listOfNutrition: List<Nutrition>,
    onEvent: (HomeScreenEvent) -> Unit
    ) {
    Card(
        modifier = Modifier
            .size(width = 400.dp, height = 400.dp)
            .padding(8.dp)
    ) {
        Text(
            text = meal,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        if (listOfNutrition.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Empty",
                    fontSize = 20.sp
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(listOfNutrition) {nutrition ->
                    NutritionItem(nutrition = nutrition, onEvent = onEvent)
                }
            }
        }
    }
}