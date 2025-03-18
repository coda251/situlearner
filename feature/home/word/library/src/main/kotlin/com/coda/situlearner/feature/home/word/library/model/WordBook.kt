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
    return listOf(
        WordBook(
            id = "",
            type = WordBookType.All,
            name = "",
            wordCount = this.size,
            coverUrl = null,
        )
    ) + this.flatMap { it.contexts }
        .groupBy { it.mediaCollection }.entries.mapNotNull { entry ->
            entry.key?.let { collection ->
                WordBook(
                    id = collection.id,
                    type = WordBookType.MediaCollection,
                    name = collection.name,
                    wordCount = entry.value.map { it.wordContext.wordId }.toSet().size,
                    coverUrl = collection.coverImageUrl,
                )
            }
        }.sortedBy { -it.wordCount }
}