package com.coda.situlearner.feature.home.word.book.model

import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.data.WordWithContexts

internal data class WordChapter(
    val id: String,
    val name: String,
    val type: WordChapterType,
    val wordCount: Int,
    val progress: Int,
    val progressType: WordProficiencyType,
)

internal fun List<WordWithContexts>.toChapters(id: String): List<WordChapter> {
    val idToWord = this.associateBy(
        keySelector = { it.word.id },
        valueTransform = { it.word }
    )
    val contexts = this.flatMap { it.contexts }.filter { it.mediaCollection?.id == id }
    val wordIds = contexts.map { it.wordContext.wordId }.toSet()

    val meaningProgress =
        idToWord.filterKeys { wordIds.contains(it) }.values.calcProgress(WordProficiencyType.Meaning)
    val totalProgress = if (meaningProgress == 100)
        idToWord.filterKeys { wordIds.contains(it) }.values.calcProgress(WordProficiencyType.Translation)
    else
        meaningProgress
    val progressType =
        if (meaningProgress == 100) WordProficiencyType.Translation else WordProficiencyType.Meaning

    return listOf(
        WordChapter(
            id = id,
            name = contexts.firstOrNull()?.mediaCollection?.name ?: "",
            type = WordChapterType.MediaCollection,
            wordCount = wordIds.size,
            progress = totalProgress,
            progressType = progressType
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
                progress = idToWord.filterKeys { chapterWordIds.contains(it) }.values.calcProgress(
                    progressType
                ),
                progressType = progressType
            )
        }
    }.sortedBy { it.name }
}

private fun Collection<Word>.calcProgress(progressType: WordProficiencyType): Int =
    this.map { it.proficiency(progressType).progress }.average().toInt().coerceIn(0, 100)

private val WordProficiency.progress: Int
    get() = when (this) {
        WordProficiency.Unset -> 0
        WordProficiency.Beginner -> 33
        WordProficiency.Intermediate -> 67
        WordProficiency.Proficient -> 100
    }