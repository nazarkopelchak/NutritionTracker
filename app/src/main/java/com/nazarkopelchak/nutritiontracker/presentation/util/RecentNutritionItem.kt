package com.nazarkopelchak.nutritiontracker.presentation.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ripple
import com.nazarkopelchak.nutritiontracker.R
import com.nazarkopelchak.nutritiontracker.domain.model.Nutrition
import com.nazarkopelchak.nutritiontracker.domain.model.RecentNutrition
import com.nazarkopelchak.nutritiontracker.presentation.util.events.NutritionHistoryEvent
import com.nazarkopelchak.nutritiontracker.ui.theme.Shapes
import com.nazarkopelchak.nutritiontracker.utils.toOneDecimal

@Composable
fun RecentNutritionItem(
    recentNutrition: RecentNutrition,
    onEvent: (NutritionHistoryEvent) -> Unit
) {
    val isDialogOpen = remember { mutableStateOf(false) }
    val isNutritionExpended = rememberSaveable { mutableStateOf(false) }
    val nutritionInteractionSource = remember { MutableInteractionSource() }
    val deleteInteractionSource = remember { MutableInteractionSource() }
    val dialogNutrition = remember { mutableStateOf(Nutrition())}

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp, 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .border(1.dp,
                        if (isNutritionExpended.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        Shapes.small)
                    .clip(Shapes.small)
                    .weight(2.5f)
                    .clickable(
                        interactionSource = nutritionInteractionSource,
                        indication = ripple(bounded = true)
                    ) {
                        isNutritionExpended.value = !isNutritionExpended.value
                    },
            ) {
                Row(
                    modifier = Modifier
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${monthOfYear(recentNutrition.date.month.name)} ${recentNutrition.date.dayOfMonth}, ${recentNutrition.date.year}",
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${recentNutrition.calories} calories",
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AnimatedVisibility(
                    visible = isNutritionExpended.value
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Protein: ${recentNutrition.protein.toOneDecimal()}g")
                            Text(text = "Sugar: ${recentNutrition.sugar.toOneDecimal()}g")
                            Text(text = "Fat: ${recentNutrition.fat.toOneDecimal()}g")
                        }


                        if (isDialogOpen.value) {
                            RecentNutritionDialog(
                                nutrition = dialogNutrition.value,
                                onDismissDialog = { isDialogOpen.value = false }
                            )
                        }

                        LazyColumn (
                            modifier = Modifier
                                .height(46.dp + 44.dp * (recentNutrition.listOfNutrition.size - 1))
                                .padding(horizontal = 8.dp)
                        ) {
                            items(recentNutrition.listOfNutrition) { nutrition ->


                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            Shapes.small
                                        )
                                        .clickable {
                                            isDialogOpen.value = true
                                            dialogNutrition.value = Nutrition(
                                                meal = nutrition.meal,
                                                foodName = nutrition.foodName,
                                                amount = nutrition.amount,
                                                measure = nutrition.measure,
                                                calories = nutrition.calories,
                                                fat = nutrition.fat,
                                                sugar = nutrition.sugar,
                                                protein = nutrition.protein
                                            )
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = nutrition.foodName!!,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp),
                                    )
                                    Text(
                                        text = "${nutrition.calories} calories",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            Row(
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, Shapes.small)
                    .height(48.dp)
                    .width(48.dp)
                    .clip(Shapes.small)
                    .clickable(
                        interactionSource = deleteInteractionSource,
                        indication = ripple(bounded = true, color = MaterialTheme.colorScheme.error)
                    ) {
                        onEvent(NutritionHistoryEvent.RemoveRecentNutritionItem(recentNutrition))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.round_clear_24),
                    contentDescription = "Delete"
                )
            }
        }
    }
}

fun monthOfYear(month: String): String {
    return month
        .lowercase()
        .replaceFirstChar { it.uppercase() }
        .substring(0, 3)
}