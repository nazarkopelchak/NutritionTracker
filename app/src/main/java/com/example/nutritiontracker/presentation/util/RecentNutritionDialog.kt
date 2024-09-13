package com.example.nutritiontracker.presentation.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.presentation.util.events.NutritionHistoryEvent
import com.example.nutritiontracker.utils.capitalized
import com.example.nutritiontracker.utils.toOneDecimal

@Composable
fun RecentNutritionDialog(
    recentNutrition: RecentNutrition,
    onDeleteDialog: (NutritionHistoryEvent) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = { onDismissDialog() }) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${recentNutrition.date.dayOfWeek.name.capitalized()},\n" +
                            "${recentNutrition.date.month.name.capitalized()} " +
                            "${recentNutrition.date.dayOfMonth}, ${recentNutrition.date.year}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(0.dp, 4.dp),    // Don't know
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Calories: ${recentNutrition.calories}kcals",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Protein: ${recentNutrition.protein?.toOneDecimal() ?: 0}g",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fat: ${recentNutrition.fat?.toOneDecimal() ?: 0}g",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sugar: ${recentNutrition.sugar?.toOneDecimal() ?: 0}g",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            onDismissDialog()
                        }) {
                        Text("Dismiss")
                    }

                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            onDeleteDialog(NutritionHistoryEvent.RemoveRecentNutritionItem(recentNutrition))
                            onDismissDialog()
                        }) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}
