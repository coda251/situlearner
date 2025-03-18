package com.coda.situlearner.feature.word.category.model

import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.data.WordWithContexts

internal data class MediaFileWithWords(
    val file: MediaFile,
    val wordWithContextsList: List<WordWithContexts>
)

internal fun <R : Comparable<R>> List<WordWithContexts>.toMediaFileWithWords(
    collectionId: String,
    wordSelector: (WordWithContexts) -> R?,
): List<MediaFileWithWords> {
    val idToWord = this.map { it.word }.associateBy { it.id }
    return this
        .flatMap { it.contexts }
        .filter { it.mediaCollection?.id == collectionId }
        .groupBy { it.mediaFile }
        .entries.mapNotNull { entry ->
            val mediaFile = entry.key
            val wordContextsView = entry.value
            mediaFile?.let { file ->
                MediaFileWithWords(
                    file = file,
                    wordWithContextsList = wordContextsView
                        .groupBy { it.wordContext.wordId }
                        .entries.mapNotNull {
                            idToWord[it.key]?.let { word ->
                                WordWithContexts(
                                    word = word,
                                    contexts = it.value
                                )
                            }
                        }
                        .sortedBy(selector = wordSelector)
                )
            }
        }
        .sortedBy { it.file.name }
}