package com.coda.situlearner.core.data.repository

import android.graphics.Bitmap
import com.coda.situlearner.core.cache.CoverImageCacheManager
import com.coda.situlearner.core.cache.SubtitleCacheManager
import com.coda.situlearner.core.data.mapper.asEntity
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.database.dao.MediaLibraryDao
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.core.model.infra.SubtitleFileContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalMediaRepository(
    private val mediaLibraryDao: MediaLibraryDao,
    private val subtitleCacheManager: SubtitleCacheManager,
    private val coverImageCacheManager: CoverImageCacheManager
) : MediaRepository {

    override fun getMediaCollections(): Flow<List<MediaCollection>> {
        return mediaLibraryDao.getMediaCollectionEntities().map {
            it.map {
                coverImageCacheManager.run { it.asExternalModel().resolveUrl() }
            }
        }
    }

    override fun getMediaCollectionWithFilesById(id: String): Flow<MediaCollectionWithFiles?> {
        return mediaLibraryDao.getMediaCollectionWithFilesEntityById(id)
            .map { it?.asExternalModel() }.map {
                it?.copy(
                    collection = coverImageCacheManager.run { it.collection.resolveUrl() },
                    files = it.files.map { subtitleCacheManager.run { it.resolveUrl() } }
                )
            }
    }

    override fun getMediaCollectionWithFilesByUrl(url: String): Flow<MediaCollectionWithFiles?> {
        return mediaLibraryDao.getMediaCollectionWithFilesEntityByUrl(url)
            .map { it?.asExternalModel() }.map {
                it?.copy(
                    collection = coverImageCacheManager.run { it.collection.resolveUrl() },
                    files = it.files.map { subtitleCacheManager.run { it.resolveUrl() } }
                )
            }
    }

    override suspend fun insertMediaCollectionWithFiles(collectionWithFiles: MediaCollectionWithFiles) {
        return mediaLibraryDao.insertMediaCollectionWithFilesEntity(
            collectionWithFiles.asEntity()
        )
    }

    override suspend fun setMediaCollectionName(id: String, name: String) {
        return mediaLibraryDao.updateMediaCollectionEntityName(id, name)
    }

    override suspend fun deleteMediaCollection(id: String) {
        mediaLibraryDao.deleteMediaCollectionEntity(id)
        subtitleCacheManager.deleteSubtitleCollectionCache(id)
        coverImageCacheManager.deleteCoverImageCache(id)
    }

    override suspend fun setMediaFilesDuration(idToDuration: Map<String, Long>) {
        return mediaLibraryDao.updateMediaFileEntities(idToDuration)
    }

    override suspend fun cacheSubtitleFile(
        collectionId: String,
        id: String,
        subtitleFileContent: SubtitleFileContent
    ) {
        subtitleCacheManager.addSubtitleCache(
            collectionId, id, subtitleFileContent
        )
    }

    override suspend fun cacheCoverImage(collectionId: String, bitmap: Bitmap) {
        coverImageCacheManager.addCoverImageCache(collectionId, bitmap)
    }
}