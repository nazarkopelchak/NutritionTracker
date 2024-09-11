package com.example.nutritiontracker.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.nutritiontracker.R
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.presentation.util.events.NutritionHistoryEvent
import com.example.nutritiontracker.ui.theme.Shapes

@Composable
fun RecentNutritionItem(
    recentNutrition: RecentNutrition,
    onEvent: (NutritionHistoryEvent) -> Unit
) {
    val isDialogOpen = rememberSaveable { mutableStateOf(false) }
    val nutritionInteractionSource = remember { MutableInteractionSource() }
    val deleteInteractionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .height(42.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDialogOpen.value) {
            RecentNutritionDialog(
                recentNutrition = recentNutrition,
                onDismissDialog = { isDialogOpen.value = false }
            )
        }

        Row(
            modifier = Modifier
                .weight(2.5f)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, Shapes.small)
                .fillMaxHeight()
                .clip(Shapes.small)
                .clickable(
                    interactionSource = nutritionInteractionSource,
                    indication = ripple(bounded = true)
                ) {
                    isDialogOpen.value = true
                },
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
        Spacer(modifier = Modifier.width(4.dp))
        Row(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.onSurface, Shapes.small)
                .fillMaxHeight()
                .width(44.dp)
                .clip(Shapes.small)
                .clickable(
                    interactionSource = deleteInteractionSource,
                    indication = ripple(bounded = true, color = MaterialTheme.colorScheme.error)
                ) { onEvent(NutritionHistoryEvent.RemoveRecentNutritionItem(recentNutrition)) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.delete_red),
                contentDescription = "Delete"
            )
        }
    }
}

fun monthOfYear(month: String): String {
    return month
        .lowercase()
        .replaceFirstChar { it.uppercase() }
        .substring(0, 3)
}