package com.coda.situlearner.infra.explorer_local.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

fun extractBitmapFrom(path: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(path)
        retriever.embeddedPicture?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}

fun Bitmap.downscale(
    maxWidth: Int = 300,
    maxHeight: Int = 300,
): Bitmap {
    val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)

    if (ratio >= 1) return this

    val newWidth = (width * ratio).toInt()
    val newHeight = (height * ratio).toInt()
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}