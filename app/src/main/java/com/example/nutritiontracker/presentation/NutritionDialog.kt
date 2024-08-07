package com.example.nutritiontracker.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nutritiontracker.domain.model.Nutrition

@Composable
fun NutritionDialog(
    nutrition: Nutrition,
    onDismissDialog: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissDialog() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nutrition.foodName,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Weight: ${nutrition.amount}${nutrition.measure}",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Calories: ${nutrition.calories}kcals",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Protein: ${nutrition.calories}g",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fat: ${nutrition.calories}g",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sugar: ${nutrition.calories}g",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

        }
    }
}