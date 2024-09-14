package com.example.nutritiontracker.presentation

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.ContentAlpha
import com.example.nutritiontracker.common.Constants
import com.example.nutritiontracker.presentation.util.SettingsTextFieldsState
import com.example.nutritiontracker.presentation.util.events.SettingsEvent
import com.example.nutritiontracker.presentation.util.events.UiEvent
import com.example.nutritiontracker.ui.theme.Shapes
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    onPopBackStack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val timePickerEnabled = rememberSaveable{ mutableStateOf(false)}
    val focusRequesters = remember { FocusableFields() }
    val uriHandler = LocalUriHandler.current

    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    // Creating a TimePicker dialog
    val timePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            viewModel.onEvent(SettingsEvent.ResetTime("$mHour:$mMinute"))
        }, hour, minute, false
    )

    timePickerDialog.setOnDismissListener { timePickerEnabled.value = false }

    LaunchedEffect(key1 = viewModel.textFieldState.value) {
        when (viewModel.textFieldState.value) {
            SettingsTextFieldsState(caloriesVisibility = true) -> focusRequesters.caloriesFocus.requestFocus()
            SettingsTextFieldsState(proteinVisibility = true) -> focusRequesters.proteinFocus.requestFocus()
            SettingsTextFieldsState(sugarVisibility = true) -> focusRequesters.sugarFocus.requestFocus()
            SettingsTextFieldsState(fatVisibility = true) -> focusRequesters.fatFocus.requestFocus()
            else -> Unit
        }
    }
    
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest {event ->
            when(event) {
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                is UiEvent.PopBackStack -> onPopBackStack()
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 24.sp,
                        text = if (viewModel.isFirstTimeRun) "Welcome" else "Settings"
                    )
                },
                navigationIcon = {
                    if (!viewModel.isFirstTimeRun) {
                        IconButton(
                            onClick = {
                                onPopBackStack()
                            }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.onEvent(SettingsEvent.OnSaveButtonClick)
                        },
                        modifier = Modifier
                            .padding(4.dp, 0.dp)) {
                        Text(text = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.onEvent(SettingsEvent.ClearAllFocus)
                },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
                    .height(52.dp)
                    .clickable {
                        viewModel.onEvent(SettingsEvent.OnMaxCaloriesRowClick)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    Text(
                        text = "Daily calories",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(visible = !viewModel.textFieldState.value.caloriesVisibility) {
                        Text(
                            text = "${viewModel.maxCalories}kcals",
                            modifier = Modifier.alpha(ContentAlpha.medium)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = viewModel.textFieldState.value.caloriesVisibility,
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = TextFieldValue(viewModel.maxCalories, selection = TextRange(viewModel.maxCalories.length)),
                        onValueChange = { textFieldValue ->
                            viewModel.onEvent(SettingsEvent.OnMaxCaloriesChange(textFieldValue.text))
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .focusRequester(focusRequesters.caloriesFocus),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        ),
                        shape = Shapes.extraLarge,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontSize = 14.sp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
                    .height(52.dp)
                    .clickable {
                        viewModel.onEvent(SettingsEvent.OnMaxProteinRowClick)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    Text(
                        text = "Daily protein",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(visible = !viewModel.textFieldState.value.proteinVisibility) {
                        Text(
                            text = "${viewModel.maxProtein}g",
                            modifier = Modifier.alpha(ContentAlpha.medium)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = viewModel.textFieldState.value.proteinVisibility,
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = TextFieldValue(viewModel.maxProtein, selection = TextRange(viewModel.maxProtein.length)),
                        onValueChange = { textFieldValue ->
                            viewModel.onEvent(SettingsEvent.OnMaxProteinChange(textFieldValue.text))
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .focusRequester(focusRequesters.proteinFocus),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        ),
                        shape = Shapes.extraLarge,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontSize = 14.sp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
                    .height(52.dp)
                    .clickable {
                        viewModel.onEvent(SettingsEvent.OnMaxSugarRowClick)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    Text(
                        text = "Daily sugar",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(visible = !viewModel.textFieldState.value.sugarVisibility) {
                        Text(
                            text = "${viewModel.maxSugar}g",
                            modifier = Modifier.alpha(ContentAlpha.medium)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = viewModel.textFieldState.value.sugarVisibility,
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = TextFieldValue(viewModel.maxSugar, selection = TextRange(viewModel.maxSugar.length)),
                        onValueChange = { textFieldValue ->
                            viewModel.onEvent(SettingsEvent.OnMaxSugarChange(textFieldValue.text))
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .focusRequester(focusRequesters.sugarFocus),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        ),
                        shape = Shapes.extraLarge,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontSize = 14.sp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
                    .height(52.dp)
                    .clickable {
                        viewModel.onEvent(SettingsEvent.OnMaxFatRowClick)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    Text(
                        text = "Daily fat",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(visible = !viewModel.textFieldState.value.fatVisibility) {
                        Text(
                            text = "${viewModel.maxFat}g",
                            modifier = Modifier.alpha(ContentAlpha.medium)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = viewModel.textFieldState.value.fatVisibility,
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = TextFieldValue(viewModel.maxFat, selection = TextRange(viewModel.maxFat.length)),
                        onValueChange = { textFieldValue ->
                            viewModel.onEvent(SettingsEvent.OnMaxFatChange(textFieldValue.text))
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .focusRequester(focusRequesters.fatFocus),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal
                        ),
                        shape = Shapes.extraLarge,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontSize = 14.sp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Reset",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.weight(4f)
                )
                Switch(
                    checked = viewModel.enableNutritionReset,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.OnNutritionResetChange) }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedVisibility(visible = viewModel.enableNutritionReset) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp)
                        .height(52.dp)
                        .clickable {
                            viewModel.onEvent(SettingsEvent.OnResetTimeRowClick)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(4f)
                    ) {
                        Text(
                            text = "Reset time",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedVisibility(visible = !viewModel.textFieldState.value.resetTimeVisibility) {
                            Text(
                                text = viewModel.resetTime,
                                modifier = Modifier.alpha(ContentAlpha.medium)
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = viewModel.textFieldState.value.resetTimeVisibility,
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .border(
                                    if (timePickerEnabled.value) OutlinedTextFieldDefaults.FocusedBorderThickness else OutlinedTextFieldDefaults.UnfocusedBorderThickness,
                                    if (timePickerEnabled.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    Shapes.extraLarge
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.resetTime,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        timePickerEnabled.value = true
                                        timePickerDialog.show()
                                    }
                                    .fillMaxWidth()
                            )

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        val link =
                            LinkAnnotation.Url(
                                Constants.NUTRITION_CALCULATOR_URL,
                                TextLinkStyles(SpanStyle(color = MaterialTheme.colorScheme.primary))
                            ) { linkAnnotation ->
                                val url = (linkAnnotation as LinkAnnotation.Url).url
                                uriHandler.openUri(url)
                            }
                        withLink(link) { append("Click here to find out your daily nutrition intake") }
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, textDecoration = TextDecoration.Underline),
                    modifier = Modifier.padding(16.dp, 0.dp)
                )
            }
        }
    }
}

private data class FocusableFields(
    val caloriesFocus: FocusRequester = FocusRequester(),
    val proteinFocus: FocusRequester = FocusRequester(),
    val sugarFocus: FocusRequester = FocusRequester(),
    val fatFocus: FocusRequester = FocusRequester()
    )

