package com.coda.situlearner.core.model.infra

data class SourceCollectionWithFiles(
    val collection: SourceCollection,
    val files: List<SourceFile>,
)