package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LineSpacer(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
    )
}