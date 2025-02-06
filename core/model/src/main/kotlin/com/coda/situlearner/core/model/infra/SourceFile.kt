package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.MediaType

data class SourceFile(
    val name: String,
    val mediaName: String,
    val mediaUrl: String,
    val mediaType: MediaType,
    val mediaSize: Long?,
    val subtitleName: String?,
    val subtitleUrl: String?,
    val subtitleSize: Long?,
    val idInDb: String? = null,
)