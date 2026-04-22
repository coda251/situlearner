package com.coda.situlearner.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.graphics.scale
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.transformations
import coil3.size.Size
import coil3.toBitmap
import coil3.transform.Transformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

suspend fun extractBitmapFrom(
    context: Context,
    model: String,
    isMedia: Boolean
): Bitmap? {
    val bitmap = if (isMedia) extractBitmapFromMedia(context, model)
    // should be an image
    else cropToSquareBitmap(context, model)

    return bitmap?.downscale()
}

suspend fun loadBitmapFromUrl(
    context: Context,
    url: String, // cached url
): Bitmap? = withContext(Dispatchers.IO) {
    val result = context.imageLoader.execute(ImageRequest.Builder(context).data(url).build())
    (result as? SuccessResult)?.image?.toBitmap()
}

private suspend fun extractBitmapFromMedia(
    context: Context,
    path: String
): Bitmap? = withContext(Dispatchers.IO) {
    val retriever = MediaMetadataRetriever()
    val imageData = try {
        retriever.setDataSource(path)
        retriever.embeddedPicture
    } catch (_: Exception) {
        null
    } finally {
        retriever.release()
    } ?: return@withContext null

    cropToSquareBitmap(context, imageData)
}

private suspend fun cropToSquareBitmap(
    context: Context,
    model: Any
): Bitmap? = withContext(Dispatchers.IO) {
    val loader = context.imageLoader

    val request = ImageRequest.Builder(context)
        .data(model)
        .size(Size.ORIGINAL)
        .transformations(object : Transformation() {
            override val cacheKey: String = "SquareCropTransformation"

            override suspend fun transform(
                input: coil3.Bitmap,
                size: Size
            ): coil3.Bitmap {
                val minEdge = min(input.width, input.height)

                // already square
                if (input.width == input.height) return input

                // otherwise
                val x = (input.width - minEdge) / 2
                val y = (input.height - minEdge) / 2

                return Bitmap.createBitmap(input, x, y, minEdge, minEdge)
            }
        })
        .allowHardware(false)
        .build()

    val result = loader.execute(request)
    (result as? SuccessResult)?.image?.toBitmap()
}

private fun Bitmap.downscale(
    maxWidth: Int = 300,
    maxHeight: Int = 300,
): Bitmap {
    val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

    if (ratio >= 1) return this

    val newWidth = (width * ratio).toInt()
    val newHeight = (height * ratio).toInt()
    return this.scale(newWidth, newHeight)
}