package com.coda.situlearner.core.model.data

import kotlinx.datetime.Instant

data class Word(
    val id: String,
    val word: String,
    val language: Language,
    val dictionaryName: String? = null,
    val pronunciation: String? = null,
    val meanings: List<WordMeaning> = emptyList(),
    val lastViewedDate: Instant? = null,
    val meaningProficiency: WordProficiency = WordProficiency.Unset,
    val translationProficiency: WordProficiency = WordProficiency.Unset
)