package com.example.nutritiontracker.presentation


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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutritiontracker.presentation.util.CircularProgressBar
import com.example.nutritiontracker.presentation.util.HomeScreenEvent
import com.example.nutritiontracker.presentation.util.NavigationItem
import com.example.nutritiontracker.presentation.util.NutritionItem
import com.example.nutritiontracker.presentation.util.HomeScreemUiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionTrackerHomeScreen(
    onNavigate: (HomeScreemUiEvent.Navigate) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val nutritions = viewModel.listOfNutritions.collectAsState(initial = emptyList())
    val totalNutrition = viewModel.totalNutrition.collectAsState()
    val snackbarState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    //val navigationItems = createNavigationItems()
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    val scrollState = rememberScrollState()
    //val config = LocalConfiguration.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {event ->
            when(event) {
                is HomeScreemUiEvent.ShowSnackbar -> {
                    val result = snackbarState.showSnackbar(event.message, event.action)
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(HomeScreenEvent.OnUndoDeleteClick)
                    }
                }
                is HomeScreemUiEvent.Navigate -> {
                    onNavigate(event)
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                createNavigationItems().forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                                Text(text = item.title)
                        },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) { item.selectedIcon }
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
                         fontFamily = FontFamily.SansSerif,
                         style = MaterialTheme.typography.headlineLarge,
                         text = "Nutrition Tracker"
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
                            percentage = 0.8f,
                            maxNumber = 100,
                            color = Color.Green,
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
                                percentage = 0.8f,
                                maxNumber = 100,
                                color = Color.Blue,
                                title = "Protein",
                                convertToInt = false,
                                fontSize = 18.sp,
                                radius = 45.dp
                            )
                        }
                        Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressBar(
                                percentage = 0.8f,
                                maxNumber = 100,
                                color = Color.Blue,
                                title = "Sugar",
                                convertToInt = false,
                                fontSize = 18.sp,
                                radius = 45.dp
                            )
                        }
                        Box(modifier = Modifier.padding(0.dp, 8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressBar(
                                percentage = 0.8f,
                                maxNumber = 100,
                                color = Color.Blue,
                                title = "Fat",
                                convertToInt = false,
                                fontSize = 18.sp,
                                radius = 45.dp
                            )
                        }
                    }
                }
                //Spacer(modifier = Modifier.height(20.dp))
//                Card(
//                    modifier = Modifier
//                        .padding(16.dp),
//                    elevation = CardDefaults.cardElevation(
//                        defaultElevation = 8.dp
//                    )
//                ) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .heightIn(Dp.Unspecified, 400.dp)
//                            .background(Color.Green)
//                    ) {
//                      }
//                    items(createMeals()) {nutrition ->
//                        NutritionItem(
//                            nutrition = nutrition
//                        )
//                    }
//                    }
//                }
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

//fun createMeals(): List<Nutrition> {
//    var n: MutableList<Nutrition> = mutableListOf()
//    for (i in 0 until 20) {
//        n.add(
//            Nutrition(
//                foodName = "Chicken",
//                amount = 30.0,
//                measure = "g",
//                calories = 144,
//                protein = 42.3,
//                fat = 8.9,
//                sugar = 0.9
//            )
//        )
//    }
//    return n
//}

fun createNavigationItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavigationItem(
            title = "History",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        ),
        NavigationItem(
            title = "Setting",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
}