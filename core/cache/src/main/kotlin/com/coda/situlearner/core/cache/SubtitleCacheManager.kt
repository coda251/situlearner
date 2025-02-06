package com.coda.situlearner.core.cache

import com.coda.situlearner.core.cache.util.assureDir
import com.coda.situlearner.core.cfg.CacheConfig
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.infra.SubtitleFileContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class SubtitleCacheManager internal constructor(private val cacheConfig: CacheConfig) {
    fun MediaFile.resolveUrl(): MediaFile {
        val file = File(cacheConfig.subtitleFilePath(this.collectionId, this.id))
        val subtitleUrl = file.takeIf { it.exists() }?.toURI()?.toURL()?.toString()
        return this.copy(subtitleUrl = subtitleUrl)
    }

    suspend fun deleteSubtitleCollectionCache(collectionId: String) {
        withContext(Dispatchers.IO) {
            val file = File(cacheConfig.subtitleCollectionPath(collectionId))
            file.deleteRecursively()
        }
    }

    suspend fun addSubtitleCache(
        collectionId: String,
        id: String,
        subtitleFileContent: SubtitleFileContent
    ) {
        withContext(Dispatchers.IO) {
            assureDir(cacheConfig.subtitleCollectionPath(collectionId))

            val file = File(cacheConfig.subtitleFilePath(collectionId, id))
            try {
                file.writeText(Json.encodeToString(subtitleFileContent))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}