package com.coda.situlearner.core.model.infra.mapper

import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.core.model.infra.SourceCollectionWithFiles
import com.coda.situlearner.core.model.infra.SourceFile
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun SourceCollection.asMediaCollection(collectionId: String) = MediaCollection(
    id = collectionId,
    name = name,
    url = url,
    originalCoverImageUrl = bitmapProviderUrl
)

@OptIn(ExperimentalUuidApi::class)
fun SourceFile.asMediaFile(collectionId: String) = MediaFile(
    id = idInDb ?: Uuid.random().toString(),
    name = name,
    collectionId = collectionId,
    url = mediaUrl,
    originalSubtitleUrl = subtitleUrl,
    mediaType = mediaType,
)

fun SourceCollectionWithFiles.asMediaCollectionWithFiles(
    collectionId: String,
): MediaCollectionWithFiles {
    val collection = collection.asMediaCollection(collectionId)
    return MediaCollectionWithFiles(
        collection = collection,
        files = files.map {
            it.asMediaFile(collectionId)
        }
    )
}

fun SourceCollection.resolveId(
    mediaCollection: MediaCollection
): SourceCollection {
    return copy(
        idInDb = mediaCollection.takeIf { it.url == this.url }?.id
    )
}

fun SourceCollectionWithFiles.resolveId(
    mediaCollectionWithFiles: MediaCollectionWithFiles
): SourceCollectionWithFiles {
    val fileUrlToId = mediaCollectionWithFiles.files.associate { Pair(it.url, it.id) }

    return copy(
        collection = collection.resolveId(mediaCollectionWithFiles.collection),
        files = files.map {
            it.copy(idInDb = fileUrlToId[it.mediaUrl])
        }
    )
}