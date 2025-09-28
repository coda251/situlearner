package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.MediaType

enum class MediaFileFormat(
    val extension: String,
    val type: MediaType,
) {
    MP3("mp3", MediaType.Audio),
    WAV("wav", MediaType.Audio),
    FLAC("flac", MediaType.Audio),
    M4A("m4a", MediaType.Audio),

    MP4("mp4", MediaType.Video),
    MKV("mkv", MediaType.Video);

    companion object {
        val extensionToType =
            entries.associateBy(keySelector = { it.extension }, valueTransform = { it.type })
    }
}