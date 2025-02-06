package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaCollectionWithFilesEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import com.coda.situlearner.core.database.model.Audio
import com.coda.situlearner.core.database.model.Video
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.database.model.MediaType as MediaTypeValue

internal fun MediaFileEntity.asExternalModel() = MediaFile(
    id = id,
    collectionId = collectionId,
    name = name,
    url = url,
    originalSubtitleUrl = subtitleUrl,
    mediaType = mediaType.asExternalModel(),
    durationInMs = durationInMs,
)

internal fun MediaFile.asEntity() = MediaFileEntity(
    id = id,
    collectionId = collectionId,
    name = name,
    url = url,
    subtitleUrl = originalSubtitleUrl,
    mediaType = mediaType.asValue(),
    durationInMs = durationInMs,
)

internal fun MediaCollectionEntity.asExternalModel() = MediaCollection(
    id = id,
    name = name,
    url = url,
    originalCoverImageUrl = coverUrl,
)

internal fun MediaCollection.asEntity() = MediaCollectionEntity(
    id = id,
    name = name,
    url = url,
    coverUrl = originalCoverImageUrl
)

internal fun MediaCollectionWithFilesEntity.asExternalModel() = MediaCollectionWithFiles(
    collection = collection.asExternalModel(),
    files = files.map(MediaFileEntity::asExternalModel)
)

internal fun MediaCollectionWithFiles.asEntity() = MediaCollectionWithFilesEntity(
    collection = collection.asEntity(),
    files = files.map(MediaFile::asEntity)
)

internal fun MediaType.asValue() = when (this) {
    MediaType.Audio -> Audio
    MediaType.Video -> Video
}

internal fun MediaTypeValue.asExternalModel() = when (this) {
    Video -> MediaType.Video
    else -> MediaType.Audio // default
}