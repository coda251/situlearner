package com.coda.situlearner.feature.word.detail.relation.model

import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.mapper.asWordInfo
import com.coda.situlearner.infra.subkit.matcher.Matcher

internal data class WordMatchResult(
    val id: String,
    val lemma: String,
    val pronunciation: String?,
    val definition: String?,
    val similarity: Double,
    val lemmaSimilarity: Double,
    val pronunciationSimilarity: Double,
    val matchedPronunciationStartIndex: Int, // inclusive
    val matchedPronunciationEndIndex: Int, // exclusive
)

internal fun matchWords(
    query: Word,
    targets: List<Word>,
    matcher: Matcher,
): List<WordMatchResult> = targets
    .map { target ->
        val lemmaSimilarity = matcher.matchLemma(query.word, target.word)
        val bestPronunciationWithSimilarity =
            query.asWordInfo().getPronunciations().flatMap { q ->
                target.asWordInfo().getPronunciations().map { t ->
                    t to matcher.matchPronunciation(q, t)
                }
            }.maxByOrNull { (_, it) -> it }
        val matchedRange = bestPronunciationWithSimilarity?.first?.let { m ->
            target.pronunciation?.indexOf(m)?.let {
                it to (it + m.length)
            }
        }
        val pronunciationSimilarity = bestPronunciationWithSimilarity?.second ?: 0.0

        WordMatchResult(
            id = target.id,
            lemma = target.word,
            pronunciation = target.pronunciation,
            definition = target.meanings.firstOrNull()?.definition,
            similarity = calculateTotalSimilarity(
                lemmaSimilarity,
                pronunciationSimilarity
            ),
            lemmaSimilarity = lemmaSimilarity,
            pronunciationSimilarity = pronunciationSimilarity,
            matchedPronunciationStartIndex = matchedRange?.first ?: -1,
            matchedPronunciationEndIndex = matchedRange?.second ?: -1,
        )
    }

private fun calculateTotalSimilarity(
    lemmaSimilarity: Double,
    pronunciationSimilarity: Double
): Double = lemmaSimilarity * 0.6 + pronunciationSimilarity * 0.4