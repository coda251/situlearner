package com.coda.situlearner.core.ui.theme

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.imageLoader
import coil3.memory.MemoryCache
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

private suspend fun getImageBitmap(
    url: String,
    context: Context,
): ImageBitmap? {
    val imageLoader = context.imageLoader

    // first get from memory
    val memoryCache = imageLoader.memoryCache
    val cacheValue = memoryCache?.get(MemoryCache.Key(url))

    return cacheValue?.image?.toBitmap()?.asImageBitmap() ?: runCatching {
        // then from storage
        val request = ImageRequest.Builder(context).data(url).build()
        (imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()?.asImageBitmap()
    }.getOrNull()
}