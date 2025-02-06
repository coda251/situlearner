package com.coda.situlearner.core.model.infra

import kotlinx.serialization.Serializable

/**
 * @param startIndex Start index of the word in original text (inclusive).
 * @param endIndex End index of the word in original text (exclusive).
 * @param lemma The lemma of the word, e.g. apples -> apple.
 */
@Serializable
data class Token(
    val startIndex: Int,
    val endIndex: Int,
    val lemma: String,
)