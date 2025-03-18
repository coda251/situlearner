package com.coda.situlearner.feature.word.category.model

import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem

internal fun List<Any>.toPlaylistItems(
    categoryType: WordCategoryType,
    categoryId: String,
) = when (categoryType) {
    WordCategoryType.All -> this.filterIsInstance<WordWithContexts>()
        .mapNotNull { it.asPlaylistItem { true } }

    WordCategoryType.MediaFile -> this.filterIsInstance<WordWithContexts>()
        .mapNotNull { wordWithContexts ->
            wordWithContexts.asPlaylistItem {
                it.mediaFile?.id == categoryId
            }
        }

    WordCategoryType.MediaCollection -> this.filterIsInstance<MediaFileWithWords>()
        .flatMap { mediaFileWithWords ->
            val mediaFileId = mediaFileWithWords.file.id
            mediaFileWithWords.wordWithContextsList.mapNotNull { wordWithContexts ->
                wordWithContexts.asPlaylistItem { it.mediaFile?.id == mediaFileId }
            }
        }
}

internal fun WordWithContexts.asPlaylistItem(
    filter: (WordContextView) -> Boolean
) = contexts.filter(filter).randomOrNull()?.let {
    val file = it.mediaFile
    val collection = it.mediaCollection
    if (file != null && collection != null) {
        Pair(collection, file).asPlaylistItem().copy(
            id = it.wordContext.id,
            clipInMs = Pair(
                first = it.wordContext.subtitleStartTimeInMs,
                second = it.wordContext.subtitleEndTimeInMs
            )
        )
    } else {
        null
    }
}