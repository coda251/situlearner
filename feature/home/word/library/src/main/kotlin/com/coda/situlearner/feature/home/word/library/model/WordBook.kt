package com.coda.situlearner.feature.home.word.library.model

import com.coda.situlearner.core.model.data.WordWithContexts

internal data class WordBook(
    val id: String,
    val type: WordBookType,
    val name: String,
    val wordCount: Int,
    val coverUrl: String?
)

internal fun List<WordWithContexts>.toWordBooks(): List<WordBook> {
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
                    coverUrl = collection.coverImageUrl,
                )
            }
        }

    return (baseBooks + mediaBooks).sortedBy { -it.wordCount }
}