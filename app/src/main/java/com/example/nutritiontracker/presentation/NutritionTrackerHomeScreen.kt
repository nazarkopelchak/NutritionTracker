package com.example.nutritiontracker.presentation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutritiontracker.R
import com.example.nutritiontracker.common.Constants
import com.example.nutritiontracker.presentation.util.CircularProgressBar
import com.example.nutritiontracker.presentation.util.NutritionItem
import com.example.nutritiontracker.presentation.util.events.HomeScreenEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.presentation.util.nav.NavigationDrawerEntries
import com.example.nutritiontracker.presentation.util.nav.NavigationItems
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionTrackerHomeScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val nutritions = viewModel.listOfNutritions.collectAsState(initial = emptyList())
    val totalNutrition = viewModel.totalNutrition.collectAsState()
    val snackbarState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val selectedItemIndex = remember { mutableIntStateOf(NavigationDrawerEntries.HomeScreenEntry) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current as? Activity
    val sharedPreferences = context?.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)

    BackHandler {
        context?.finish()
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
                        selected = index == selectedItemIndex.intValue,
                        onClick = {
                            viewModel.onEvent(HomeScreenEvent.OnNavigationItemClick(item.route))
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex.intValue) { item.selectedIcon }
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
                 TopAppBar(title = {
                     Text(
                         style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.sp),
                         text = "NutritionTracker",
                         color = MaterialTheme.colorScheme.primary
                     )
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
                        text = "There seems to be nothing here.",
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
                    //.height((config.screenHeightDp).dp) //FIXIT
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                            CircularProgressBar(
                                percentage = totalNutrition.value.totalCalories.toFloat() / sharedPreferences?.getString(Constants.MAX_CALORIES, "2000")!!.toFloat(),
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
                                    percentage = totalNutrition.value.totalProtein.toFloat() / sharedPreferences?.getString(Constants.MAX_PROTEIN, "60")!!.toFloat(),
                                    maxNumber = sharedPreferences.getString(Constants.MAX_PROTEIN, "60")!!.toInt(),
                                    color = Color(android.graphics.Color.parseColor("#0000e6")),
                                    title = "Protein",
                                    convertToInt = false,
                                    fontSize = 18.sp,
                                    radius = 45.dp
                                )
                            }
                            Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressBar(
                                    percentage = totalNutrition.value.totalSugar.toFloat() / sharedPreferences?.getString(Constants.MAX_SUGAR, "30")!!.toFloat(),
                                    maxNumber = sharedPreferences.getString(Constants.MAX_SUGAR, "30")!!.toInt(),
                                    color = Color(android.graphics.Color.parseColor("#0000cc")),
                                    title = "Sugar",
                                    convertToInt = false,
                                    fontSize = 18.sp,
                                    radius = 45.dp
                                )
                            }
                            Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressBar(
                                    percentage = totalNutrition.value.totalFat.toFloat() / sharedPreferences?.getString(Constants.MAX_FAT, "60")!!.toFloat(),
                                    maxNumber = sharedPreferences.getString(Constants.MAX_FAT, "60")!!.toInt(),
                                    color = Color(android.graphics.Color.parseColor("#0000b3")),
                                    title = "Fat",
                                    convertToInt = false,
                                    fontSize = 18.sp,
                                    radius = 45.dp
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 0.dp, vertical = 16.dp)
                            .fillMaxWidth()
                            .heightIn(Dp.Unspecified, 400.dp)
                    ) {
                        items(nutritions.value) {nutrition ->
                            NutritionItem(nutrition = nutrition, onEvent = viewModel::onEvent)
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }


        }
    }
}