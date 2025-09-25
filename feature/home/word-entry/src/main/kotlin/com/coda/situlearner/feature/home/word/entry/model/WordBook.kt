package com.coda.situlearner.feature.home.word.entry.model

import com.coda.situlearner.core.model.data.WordBookSortBy
import com.coda.situlearner.core.model.data.WordWithContexts
import kotlin.time.Instant

internal data class WordBook(
    val id: String,
    val type: WordBookType,
    val name: String,
    val wordCount: Int,
    val updatedDate: Instant,
    val coverUrl: String?
)

internal fun List<WordWithContexts>.toWordBooks(wordBookSortBy: WordBookSortBy): List<WordBook> {
    val noMediaCount = this.count { wordWithContexts ->
        wordWithContexts.contexts.all { it.mediaCollection == null }
    }

    val baseBooks = buildList {
        add(
            WordBook(
                id = "",
                type = WordBookType.All,
                name = "",
                wordCount = this@toWordBooks.size,
                updatedDate = Instant.Companion.DISTANT_FUTURE,
                coverUrl = null,
            )
        )

        if (noMediaCount > 0) {
            add(
                WordBook(
                    id = "",
                    type = WordBookType.NoMedia,
                    name = "",
                    wordCount = noMediaCount,
                    updatedDate = Instant.Companion.DISTANT_PAST,
                    coverUrl = null,
                )
            )
        }
    }

    val mediaBooks = this
        .flatMap { it.contexts }
        .groupBy { it.mediaCollection }
        .entries.mapNotNull { entry ->
            entry.key?.let { collection ->
                WordBook(
                    id = collection.id,
                    type = WordBookType.MediaCollection,
                    name = collection.name,
                    wordCount = entry.value.map { it.wordContext.wordId }.toSet().size,
                    updatedDate = entry.value.maxOf { it.wordContext.createdDate },
                    coverUrl = collection.coverImageUrl,
                )
            }
        }

    return (baseBooks + mediaBooks).sortedBy(wordBookSortBy)
}

private fun List<WordBook>.sortedBy(wordBookSortBy: WordBookSortBy) = when (wordBookSortBy) {
    WordBookSortBy.Count -> sortedByDescending { it.wordCount }
    WordBookSortBy.UpdatedDate -> sortedByDescending { it.updatedDate }
}