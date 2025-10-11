package com.coda.situlearner.core.model.data

import kotlin.time.Clock
import kotlin.time.Instant

data class Word(
    val id: String,
    val word: String,
    val language: Language,
    val dictionaryName: String? = null,
    val pronunciation: String? = null,
    val meanings: List<WordMeaning> = emptyList(),
    val lastViewedDate: Instant? = null,
    val createdDate: Instant = Clock.System.now(),
    val meaningProficiency: WordProficiency = WordProficiency.Unset,
    val translationProficiency: WordProficiency = WordProficiency.Unset
) {
    fun proficiency(type: WordProficiencyType) = when (type) {
        WordProficiencyType.Meaning -> meaningProficiency
        WordProficiencyType.Translation -> translationProficiency
    }
}