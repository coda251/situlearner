package com.coda.situlearner.core.model.data

data class MediaFile(
    val id: String,
    val collectionId: String,
    val name: String,
    val url: String,
    val subtitleUrl: String? = null,
    val originalSubtitleUrl: String? = null,
    val mediaType: MediaType,
    val durationInMs: Long? = null,
)