package com.example.nutritiontracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutritiontracker.presentation.util.AddNutritionEvent
import com.example.nutritiontracker.presentation.util.NutritionDialog
import com.example.nutritiontracker.presentation.util.NutritionTextFields
import com.example.nutritiontracker.presentation.util.UiEvent
import com.example.nutritiontracker.ui.theme.Shapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNutritionScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddNutritionViewModel = hiltViewModel()
) {
    val snackbarState = remember { SnackbarHostState() }
    val expanded = remember { mutableStateOf(false) }
    val state = viewModel.uiState.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect{ event ->
            when(event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    snackbarState.currentSnackbarData?.dismiss()
                    snackbarState.showSnackbar(event.message)
                }
                else -> Unit
            }
        }
    }
    
   Scaffold (
       modifier = Modifier.fillMaxSize(),
       topBar = {
           TopAppBar(title = {
               Text(
                   fontFamily = FontFamily.SansSerif,
                   style = MaterialTheme.typography.headlineLarge,
                   text = "Add Nutrition"
               )
           },
               navigationIcon = {
                   IconButton(onClick = {
                       onPopBackStack()
                   }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                   }
               },
               modifier = Modifier
           )
       },
       snackbarHost = { SnackbarHost(hostState = snackbarState) }
   ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (state.showDialog) {
                NutritionDialog(
                    nutrition = viewModel.nutrition,
                    enableButtons = true,
                    onConfirmDialog = viewModel::onEvent,
                    onDismissDialog = {
                        viewModel.onEvent(AddNutritionEvent.OnDismissButtonClick)
                    })
            }
            OutlinedTextField(
                value = viewModel.foodQuery,
                onValueChange = { title ->
                    viewModel.onEvent(AddNutritionEvent.OnFoodQueryChange(title))
                },
                placeholder = {
                    Text(
                        text = if (state.customModeOn) "Ex: Chicken wings" else "Ex: 120g of chicken wings"
                    )
                },
                singleLine = true,
                isError = state.errorTextField == NutritionTextFields.FoodQueryField,
                shape = Shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 4.dp)
            )
            AnimatedVisibility(state.customModeOn) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.amount,
                            onValueChange = {
                                viewModel.onEvent(AddNutritionEvent.OnAmountChange(it))
                            },
                            shape = Shapes.extraLarge,
                            placeholder = {
                                Text(text = "Amount")
                            },
                            isError = state.errorTextField == NutritionTextFields.AmountField,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier
                                .padding(24.dp, 4.dp, 4.dp, 4.dp)
                                .weight(2f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp, 4.dp, 24.dp, 4.dp)
                                .weight(1f)
                                .border(
                                    if (expanded.value) OutlinedTextFieldDefaults.FocusedBorderThickness else OutlinedTextFieldDefaults.UnfocusedBorderThickness,
                                    if (expanded.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    Shapes.extraLarge
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.units,
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clickable(interactionSource = interactionSource, indication = null) {
                                        expanded.value = true
                                        focusManager.clearFocus()
                                    }
                                    .fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) { // Make selected dropDownItem disappear
                                DropdownMenuItem(
                                    text = { Text("g") },
                                    onClick = {
                                        viewModel.onEvent(AddNutritionEvent.OnUnitsChange("g"))
                                        expanded.value = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "oz" )},
                                    onClick = {
                                        viewModel.onEvent(AddNutritionEvent.OnUnitsChange("oz"))
                                        expanded.value = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "lb") },
                                    onClick = {
                                        viewModel.onEvent(AddNutritionEvent.OnUnitsChange("lb"))
                                        expanded.value = false
                                    }
                                )
                            }
                        }
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = viewModel.calories,
                            onValueChange = {
                                viewModel.onEvent(AddNutritionEvent.OnCaloriesChange(it))
                            },
                            shape = Shapes.extraLarge,
                            placeholder = {
                                Text(text = "Calories")
                            },
                            isError = state.errorTextField == NutritionTextFields.CaloriesField,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .padding(24.dp, 4.dp, 4.dp, 4.dp)
                                .weight(1f)
                        )
                        OutlinedTextField(
                            value = viewModel.protein,
                            onValueChange = {
                                viewModel.onEvent(AddNutritionEvent.OnProteinChange(it))
                            },
                            shape = Shapes.extraLarge,
                            placeholder = {
                                Text(text = "Protein")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier
                                .padding(4.dp, 4.dp, 24.dp, 4.dp)
                                .weight(1f)
                        )
                    }
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = viewModel.sugar,
                            onValueChange = {
                                viewModel.onEvent(AddNutritionEvent.OnSugarChange(it))
                            },
                            shape = Shapes.extraLarge,
                            placeholder = {
                                Text(text = "Sugar")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier
                                .padding(24.dp, 4.dp, 4.dp, 4.dp)
                                .weight(1f)
                        )
                        OutlinedTextField(
                            value = viewModel.fat,
                            onValueChange = {
                                viewModel.onEvent(AddNutritionEvent.OnFatChange(it))
                            },
                            shape = Shapes.extraLarge,
                            placeholder = {
                                Text(text = "Fat")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Decimal
                            ),
                            modifier = Modifier
                                .padding(4.dp, 4.dp, 24.dp, 4.dp)
                                .weight(1f)
                        )
                    }
                }

            }

            FilterChip(
                selected = state.customModeOn, //state.customModeOn
                onClick = {
                    viewModel.onEvent(AddNutritionEvent.OnCustomModeClick) },
                label = { Text(text = "Custom mode") }
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.onEvent(AddNutritionEvent.OnButtonClick)
                }) {
                Text(text = if (state.customModeOn) "Save" else "Search")
            }
        }
   } 
}