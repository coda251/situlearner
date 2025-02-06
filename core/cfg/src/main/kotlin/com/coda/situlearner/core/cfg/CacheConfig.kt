package com.coda.situlearner.core.cfg

class CacheConfig(basePath: String) {

    private val subtitleBasePath = "$basePath/subtitles"

    val imageBasePath = "$basePath/image"

    fun subtitleCollectionPath(collectionId: String) = "$subtitleBasePath/$collectionId"

    fun subtitleFilePath(collectionId: String, fileId: String) =
        "$subtitleBasePath/$collectionId/$fileId"

    fun imageFilePath(collectionId: String) = "$imageBasePath/$collectionId"
}