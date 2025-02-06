package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.WordMeaning

data class WordInfo(
    val word: String,
    val dictionaryName: String,
    val pronunciation: String? = null,
    val meanings: List<WordMeaning>? = null,
)