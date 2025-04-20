package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.WordMeaning

@Suppress("DataClassPrivateConstructor")
data class WordInfo private constructor(
    val word: String,
    val dictionaryName: String?,
    val pronunciation: String? = null,
    val meanings: List<WordMeaning> = emptyList(),
) {
    companion object {
        fun fromDb(
            word: String,
            dictionaryName: String?,
            pronunciation: String?,
            meanings: List<WordMeaning>,
        ) = WordInfo(
            word = word,
            dictionaryName = dictionaryName,
            pronunciation = pronunciation,
            meanings = meanings,
        )

        fun fromWebOrUser(
            word: String,
            dictionaryName: String?,
            pronunciations: List<String>,
            meanings: List<WordMeaning>,
        ) = WordInfo(
            word = word,
            dictionaryName = dictionaryName,
            pronunciation = pronunciations.merge(),
            meanings = meanings.simplify(),
        )
    }

    fun getPronunciations(): List<String> {
        return pronunciation?.split(PRONUNCIATION_SEPARATOR) ?: emptyList()
    }
}

/**
 * Simplify the input meanings. This function assures that:
 *  - no duplicate pos tags in the meaning list. If duplicate tags are detected,
 *      the meanings will be joined by "\n"
 *  - no blank definition in each meaning
 */
private fun List<WordMeaning>.simplify() = this
    .groupBy { it.partOfSpeechTag.trim() }
    .mapValues { (_, meanings) ->
        meanings.mapNotNull { it.definition.trim().ifBlank { null } }.joinToString("\n")
    }
    .filterValues { it.isNotBlank() }
    .map { (pos, def) -> WordMeaning(pos, def) }

private const val PRONUNCIATION_SEPARATOR = " | "

/**
 * Merge the input pronunciations into a single string using [PRONUNCIATION_SEPARATOR]
 * as separator. This function assures that:
 * - no duplicate pronunciations
 * - no empty pronunciation
 */
private fun List<String>.merge() = this
    .distinct()
    .mapNotNull { it.ifBlank { null } }
    .ifEmpty { null }
    ?.joinToString(PRONUNCIATION_SEPARATOR)