package com.coda.situlearner.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.materialkolor.DynamicMaterialTheme

@Composable
fun SituLearnerTheme(
    useDarkTheme: Boolean,
    colorMode: ThemeColorMode,
    themeColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val seedColor = when (colorMode) {
        ThemeColorMode.DynamicWithWallpaper -> {
            val colorScheme = if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
            colorScheme.primary
        }

        else -> themeColor
    }

    // Note: laggy in debug mode, use release mode
    DynamicMaterialTheme(
        seedColor = seedColor,
        useDarkTheme = useDarkTheme,
        animate = true,
        content = content,
    )
}