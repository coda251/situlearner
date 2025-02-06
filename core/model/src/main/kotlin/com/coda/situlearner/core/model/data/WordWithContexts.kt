package com.coda.situlearner.core.model.data

data class WordWithContexts(
    val word: Word,
    val contexts: List<WordContextView>,
)