package com.coda.situlearner.core.data.repository

import android.graphics.Bitmap
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.core.model.infra.SubtitleFileContent
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun getMediaCollections(): Flow<List<MediaCollection>>

    fun getMediaCollectionWithFilesById(id: String): Flow<MediaCollectionWithFiles?>

    fun getMediaCollectionWithFilesByUrl(url: String): Flow<MediaCollectionWithFiles?>

    suspend fun insertMediaCollectionWithFiles(collectionWithFiles: MediaCollectionWithFiles)

    suspend fun setMediaCollectionName(id: String, name: String)

    suspend fun deleteMediaCollection(id: String)

    suspend fun setMediaFilesDuration(idToDuration: Map<String, Long>)

    suspend fun cacheSubtitleFile(
        collectionId: String,
        id: String,
        subtitleFileContent: SubtitleFileContent
    )

    suspend fun cacheCoverImage(
        collectionId: String,
        bitmap: Bitmap
    )
}