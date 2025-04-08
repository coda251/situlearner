package com.coda.situlearner.feature.word.list.model

import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.data.mapper.asPlaylistItem
import com.coda.situlearner.core.model.feature.WordListType

internal fun List<WordWithContexts>.toPlaylistItems(
    wordListType: WordListType,
    id: String?,
) = when (wordListType) {
    WordListType.All -> this.mapNotNull { it.contexts.randomOrNull()?.asPlaylistItem() }
    WordListType.MediaFile -> this.mapNotNull { wordWithContexts ->
        wordWithContexts.contexts.filter { it.mediaFile?.id == id }.randomOrNull()?.asPlaylistItem()
    }

    WordListType.MediaCollection -> this.mapNotNull { wordWithContexts ->
        wordWithContexts.contexts.filter { it.mediaCollection?.id == id }.randomOrNull()
            ?.asPlaylistItem()
    }
    WordListType.NoMedia -> emptyList()
}