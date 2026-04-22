package com.coda.situlearner.core.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.coda.situlearner.core.ui.util.loadBitmapFromUrl
import com.materialkolor.ktx.themeColorOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun themeColorFromImage(
    url: String,
    context: Context
): Color? = withContext(Dispatchers.IO) {
    loadBitmapFromUrl(context, url)?.asImageBitmap()?.themeColorOrNull()
}