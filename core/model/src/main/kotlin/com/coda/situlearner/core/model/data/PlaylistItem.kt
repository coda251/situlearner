package com.coda.situlearner.core.model.data

data class PlaylistItem(
    val id: String,
    val name: String,
    val collectionName: String,
    val mediaUrl: String,
    val subtitleUrl: String?,
    val thumbnailUrl: String?,
    val mediaType: MediaType,
    val durationInMs: Long? = null,
    val lastPositionInMs: Long? = null,
    val clipInMs: Pair<Long, Long>? = null
)