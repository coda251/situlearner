package com.coda.situlearner.feature.home.media.edit.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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

internal class ExtractBitmapUseCase(private val context: Context) {
    suspend operator fun invoke(uri: Uri): Bitmap? = getCoilProcessedBitmap(context, uri)

    private suspend fun getCoilProcessedBitmap(
        context: Context,
        uri: Uri,
    ): Bitmap? = withContext(Dispatchers.IO) {
        val loader = context.imageLoader

        val request = ImageRequest.Builder(context)
            .data(uri)
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
}