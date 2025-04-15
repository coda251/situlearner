package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.WordMeaning

data class WordInfo(
    val word: String,
    val dictionaryName: String?,
    val pronunciation: String? = null,
    val meanings: List<WordMeaning>? = null,
) {
    companion object {
        private const val PRONUNCIATION_SEPARATOR = " | "
    }

    constructor(
        word: String,
        dictionaryName: String?,
        pronunciations: List<String>,
        meanings: List<WordMeaning>?,
    ) : this(
        word = word,
        dictionaryName = dictionaryName,
        pronunciation = pronunciations.distinct().takeIf { it.isNotEmpty() }
            ?.joinToString(PRONUNCIATION_SEPARATOR),
        meanings = meanings,
    )

    fun getPronunciations(): List<String> {
        return pronunciation?.split(PRONUNCIATION_SEPARATOR) ?: emptyList()
    }
}