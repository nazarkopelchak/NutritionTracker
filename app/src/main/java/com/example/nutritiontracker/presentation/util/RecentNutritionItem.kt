package com.example.nutritiontracker.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nutritiontracker.R
import com.example.nutritiontracker.domain.model.RecentNutrition
import com.example.nutritiontracker.ui.theme.Shapes

@Composable
fun RecentNutritionItem(
    recentNutrition: RecentNutrition,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 2.dp)
            .height(42.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(2.5f)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, Shapes.small)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recentNutrition.date.toString(),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${recentNutrition.calories.toString()} calories",
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
        ) {
            IconButton(
                onClick = {
                /*TODO*/
                }) {
                Image(
                    painter = painterResource(id = R.drawable.delete_red),
                    contentDescription = "Delete"
                )
            }
        }
    }
}