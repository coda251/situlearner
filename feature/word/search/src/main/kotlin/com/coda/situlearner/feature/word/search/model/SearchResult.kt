package com.coda.situlearner.feature.word.search.model

import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.infra.Subtitle

data class SearchResult(
    val collection: MediaCollection,
    val file: MediaFile,
    val subtitle: Subtitle,
    val start: Int,
    val end: Int,
)
