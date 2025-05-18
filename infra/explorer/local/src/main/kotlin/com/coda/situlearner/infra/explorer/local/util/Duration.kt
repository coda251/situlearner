package com.coda.situlearner.infra.explorer.local.util

import android.media.MediaMetadataRetriever

fun getDurations(paths: Set<String>): Map<String, Long?> {
    val retriever = MediaMetadataRetriever()

    val mapper = buildMap {
        paths.onEach {
            this[it] = try {
                retriever.setDataSource(it)
                val durationStr =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationStr?.toLong()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    retriever.release()

    return mapper
}