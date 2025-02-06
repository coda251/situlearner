package com.coda.situlearner.core.cache

import android.graphics.Bitmap
import com.coda.situlearner.core.cache.util.assureDir
import com.coda.situlearner.core.cfg.CacheConfig
import com.coda.situlearner.core.model.data.MediaCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CoverImageCacheManager internal constructor(private val cacheConfig: CacheConfig) {
    fun MediaCollection.resolveUrl(): MediaCollection {
        val file = File(cacheConfig.imageFilePath(this.id))
        val coverImageUrl = file.takeIf { it.exists() }?.toURI()?.toURL()?.toString()
        return this.copy(coverImageUrl = coverImageUrl)
    }

    fun deleteCoverImageCache(collectionId: String) {
        val file = File(cacheConfig.imageFilePath(collectionId))
        file.delete()
    }

    suspend fun addCoverImageCache(collectionId: String, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            assureDir(cacheConfig.imageBasePath)

            val file = File(cacheConfig.imageFilePath(collectionId))
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            }
        }
    }
}