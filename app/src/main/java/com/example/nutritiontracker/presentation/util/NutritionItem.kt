package com.example.nutritiontracker.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutritiontracker.R
import com.example.nutritiontracker.domain.model.Nutrition
import com.example.nutritiontracker.presentation.util.events.HomeScreenEvent
import com.example.nutritiontracker.utils.toOneDecimal

@Composable
fun NutritionItem(
    nutrition: Nutrition,
    onEvent: (HomeScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDialogOpen = rememberSaveable { mutableStateOf(false)}

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                isDialogOpen.value = true
            }
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        if (isDialogOpen.value) {
            NutritionDialog(
                nutrition = nutrition,
                onDeleteDialog = onEvent
            ) {
                isDialogOpen.value = false
            }
        }
        Text(
            modifier = modifier.weight(2f),
            text = nutrition.foodName!!,
            fontSize = 18.sp,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = modifier.weight(1f),
            fontSize = 18.sp,
            text = (nutrition.amount?.toOneDecimal() ?: 0).toString() + nutrition.measure,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = {
                onEvent(HomeScreenEvent.RemoveNutritionItem(nutrition))
        }
        ) {
            Image(
                painter = painterResource(id = R.drawable.round_clear_24),
                contentDescription = null
            )
        }
    }
}