package com.coda.situlearner.core.data.util

import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts

internal fun selectRecommendedWords(
    words: List<WordWithContexts>,
    count: Int,
): List<WordWithContexts> {
    val proficiencyToWords =
        words.filter { it.contexts.isNotEmpty() }.groupBy { it.word.meaningProficiency }
    val proficiencyToCount = computeQuotasByProficiency(count)

    val layers = mutableListOf<List<WordWithContexts>>()
    val quotas = mutableListOf<Int>()
    var remainder = 0

    WordProficiency.entries.sortedByDescending { it.quota }.forEach {
        val layer = proficiencyToWords[it]
        val expectedQuota = proficiencyToCount[it]
        if (layer == null && expectedQuota != null) {
            remainder += expectedQuota
        } else if (layer != null && expectedQuota != null) {
            val actualQuota = minOf(layer.size, expectedQuota)
            layers.add(layer)
            quotas.add(actualQuota)
            remainder += expectedQuota - actualQuota
        }
    }

    if (quotas.isNotEmpty()) quotas[0] += remainder

    return layeredQuotaWords(layers, quotas).map {
        it.copy(
            word = it.word,
            contexts = listOf(it.contexts.random())
        )
    }
}

private fun layeredQuotaWords(
    layers: List<List<WordWithContexts>>,
    quotas: List<Int>
): List<WordWithContexts> {
    require(layers.size == quotas.size) { "Layers and quotas must have same size" }

    val result = mutableListOf<WordWithContexts>()
    var carryOver = 0

    for ((index, layer) in layers.withIndex()) {
        val desired = quotas[index] + carryOver
        val actual = minOf(desired, layer.size)

        result.addAll(layer.shuffled().take(actual))
        carryOver = desired - actual
    }

    return result
}

private val WordProficiency.quota: Float
    get() = when (this) {
        WordProficiency.Unset -> 0.4f
        WordProficiency.Beginner -> 0.3f
        WordProficiency.Intermediate -> 0.2f
        WordProficiency.Proficient -> 0.1f
    }

private fun computeQuotasByProficiency(count: Int): Map<WordProficiency, Int> {
    val levels = WordProficiency.entries

    val intQuotas =
        levels.associateWith { it.quota * count }.mapValues { it.value.toInt() }.toMutableMap()
    var remainder = count - intQuotas.values.sum()

    val sortedByQuota = levels.sortedByDescending { it.quota }

    for (level in sortedByQuota) {
        if (remainder <= 0) break
        intQuotas[level] = intQuotas.getOrDefault(level, 0) + 1
        remainder--
    }

    return intQuotas
}