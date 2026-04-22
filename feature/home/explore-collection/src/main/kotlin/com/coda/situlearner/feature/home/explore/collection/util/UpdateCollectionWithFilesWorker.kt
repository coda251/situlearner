package com.coda.situlearner.feature.home.explore.collection.util

import android.content.Context
import androidx.core.net.toUri
import com.coda.situlearner.core.data.repository.MediaRepository
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.infra.MediaFileFormat
import com.coda.situlearner.core.ui.util.extractBitmapFrom
import com.coda.situlearner.infra.subkit.lang_detector.LanguageDetector
import com.coda.situlearner.infra.subkit.processor.Processor
import com.coda.situlearner.infra.subkit.tokenizer.Tokenizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

internal class UpdateCollectionWithFilesWorker(
    // params
    private val sourceLanguage: Language,
    private val targetLanguage: Language,
    private val collectionId: String,
    private val mediaRepository: MediaRepository,
    private val processor: Processor,
    private val context: Context
) {

    suspend fun doWork() {
        // get data from repo
        val collectionWithFiles =
            mediaRepository.getMediaCollectionWithFilesById(collectionId).firstOrNull()
                ?: return

        // 1. cover
        saveCoverImage(collectionWithFiles.collection)

        // 2. subtitles
        processSubtitleFiles(collectionWithFiles.files, sourceLanguage, targetLanguage)

        // 3. media duration
        // NOTE: we make this as the last step to update db and
        // emit new data with subtitle file already cached (as a workaround solution).
        val idToDuration = extractMediaDuration(collectionWithFiles.files)
        mediaRepository.setMediaFilesDuration(idToDuration)
    }

    private suspend fun processSubtitleFiles(
        mediaFiles: List<MediaFile>,
        sourceLanguage: Language,
        targetLanguage: Language
    ) = withContext(Dispatchers.IO) {
        val tokenizer = Tokenizer.getTokenizer(sourceLanguage) ?: return@withContext
        val languageDetector = LanguageDetector.getInstance()

        mediaFiles.filter { it.subtitleUrl == null } // does not have cached subtitle
            .forEach { mediaFile ->
                val subtitlePath = mediaFile.originalSubtitleUrl?.toUri()?.path
                subtitlePath?.let { s ->
                    val content = processor.process(
                        s,
                        sourceLanguage,
                        targetLanguage,
                        tokenizer,
                        languageDetector
                    )
                    content?.let {
                        mediaRepository.cacheSubtitleFile(
                            mediaFile.collectionId, mediaFile.id, it
                        )
                    }
                }
            }
    }

    private suspend fun extractMediaDuration(mediaFiles: List<MediaFile>): Map<String, Long> {
        val pathToId = mediaFiles.mapNotNull {
            if (it.durationInMs != null) null
            else {
                val path = it.url.toUri().path
                path?.let { p -> Pair(p, it.id) }
            }
        }.toMap()

        if (pathToId.isEmpty()) return emptyMap()

        val pathToDuration = withContext(Dispatchers.IO) {
            getDurations(pathToId.keys)
        }

        return buildMap {
            pathToDuration.entries.forEach {
                val (path, duration) = it
                duration?.let {
                    pathToId[path]?.let { id ->
                        this[id] = duration
                    }
                }
            }
        }
    }

    private suspend fun saveCoverImage(mediaCollection: MediaCollection) {
        if (mediaCollection.coverImageUrl != null) return

        withContext(Dispatchers.IO) {
            runCatching {
                mediaCollection.originalCoverImageUrl?.let {
                    // url used as path (absolute path) if the cover image is never cached
                    val extension = it.substringAfterLast('.', "").lowercase()
                    val isMedia = extension in MediaFileFormat.extensionToType.keys
                    extractBitmapFrom(context, it, isMedia)?.run {
                        mediaRepository.cacheCoverImage(collectionId, this)
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}