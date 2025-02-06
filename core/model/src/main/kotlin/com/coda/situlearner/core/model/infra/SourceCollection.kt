package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.MediaType

data class SourceCollection(
    val name: String,
    val url: String,
    val mediaType: MediaType? = null,
    val bitmapProviderUrl: String? = null,
    val idInDb: String? = null,
)