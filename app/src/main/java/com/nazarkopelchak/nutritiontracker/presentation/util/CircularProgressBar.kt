package com.nazarkopelchak.nutritiontracker.presentation.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazarkopelchak.nutritiontracker.utils.toOneDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CircularProgressBar(
    percentage: Float,
    maxNumber: Int,
    color: Color,
    title: String? = null,
    convertToInt: Boolean = true,
    fontSize: TextUnit = 28.sp,
    radius: Dp = 50.dp,
    strokeWidth: Dp = 8.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
){
    // this variable makes sure that if maxNumber is 0, this function would still display the amount correctly
    val zeroOffset = if (maxNumber == 0) { 1 } else 0

    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        ),
        label = "Float animation"
    )
    LaunchedEffect(key1 = true) {
        coroutineScope {
            launch(Dispatchers.Default) {
                delay(250L) // Gives it a small delay before playing the animation
                animationPlayed = true
            }
        }
    }

    Box(
        modifier = Modifier.size(radius * 2f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = if (currPercentage.value >= 1.0f && zeroOffset == 0) Color.Red else color,
                startAngle = -90f,
                sweepAngle = 360 * currPercentage.value,
                useCenter = false,
                style = Stroke(
                    strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            title?.let {
                Text(
                    text = it,
                    fontSize = fontSize * 0.7,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = if (convertToInt) (currPercentage.value * (maxNumber + zeroOffset)).toInt().toString()
                else (currPercentage.value * (maxNumber + zeroOffset)).toDouble().toOneDecimal().toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}