package com.coda.situlearner.feature.restore.navigation

import androidx.compose.runtime.Composable
import com.coda.situlearner.feature.restore.RestoreScreen

@Composable
fun RestoreScreen(
    onNavigateToMainScreen: () -> Unit
) {
    RestoreScreen(
        onFinished = onNavigateToMainScreen
    )
}