package com.coda.situlearner.core.model.data

data class MediaCollection(
    val id: String,
    val name: String,
    val url: String,
    val coverImageUrl: String? = null,
    val originalCoverImageUrl: String? = null,
)