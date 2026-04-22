package com.coda.situlearner.core.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.materialkolor.ktx.themeColorOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun themeColorFromImage(
    url: String,
    context: Context
): Color? = withContext(Dispatchers.IO) {
    getImageBitmap(url, context)?.themeColorOrNull()
}

suspend fun getImageBitmap(
    url: String,
    context: Context,
): ImageBitmap? {
    val result = context.imageLoader.execute(ImageRequest.Builder(context).data(url).build())
    val bitmap = (result as? SuccessResult)?.image?.toBitmap()?.asImageBitmap()
    return bitmap
}