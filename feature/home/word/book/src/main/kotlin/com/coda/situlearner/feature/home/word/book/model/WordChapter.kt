package com.coda.situlearner.feature.home.word.book.model

import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts

internal data class WordChapter(
    val id: String,
    val name: String,
    val type: WordChapterType,
    val wordCount: Int,
    val progress: Int,
)

internal fun List<WordWithContexts>.toChapters(id: String): List<WordChapter> {
    val idToWord = this.associateBy(
        keySelector = { it.word.id },
        valueTransform = { it.word }
    )
    val contexts = this.flatMap { it.contexts }.filter { it.mediaCollection?.id == id }
    val wordIds = contexts.map { it.wordContext.wordId }.toSet()

    return listOf(
        WordChapter(
            id = id,
            name = contexts.firstOrNull()?.mediaCollection?.name ?: "",
            type = WordChapterType.MediaCollection,
            wordCount = wordIds.size,
            progress = idToWord.filterKeys { wordIds.contains(it) }.values.progress
        )
    ) + contexts.groupBy { it.mediaFile }.entries.mapNotNull { entry ->
        val mediaFile = entry.key
        val chapterWordIds = entry.value.map { it.wordContext.wordId }.toSet()

        mediaFile?.let { file ->
            WordChapter(
                id = file.id,
                name = file.name,
                type = WordChapterType.MediaFile,
                wordCount = chapterWordIds.size,
                progress = idToWord.filterKeys { chapterWordIds.contains(it) }.values.progress
            )
        }
    }
}

private val Collection<Word>.progress: Int
    get() = this.map { it.proficiency.progress }.average().toInt().coerceIn(0, 100)

private val WordProficiency.progress: Int
    get() = when (this) {
        WordProficiency.Unset -> 0
        WordProficiency.Beginner -> 33
        WordProficiency.Intermediate -> 67
        WordProficiency.Proficient -> 100
    }