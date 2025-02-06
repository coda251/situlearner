package com.coda.situlearner.feature.player.entry.widgets.subtitle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CenterIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    thickness: Dp = 1.dp
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(thickness)
        ) {
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to color, 0.1f to Color.Transparent, 0.9f to Color.Transparent, 1f to color
                )
            )
        }
    }
}