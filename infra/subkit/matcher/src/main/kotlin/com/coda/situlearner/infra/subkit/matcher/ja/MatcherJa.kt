package com.coda.situlearner.infra.subkit.matcher.ja

import com.coda.situlearner.infra.subkit.matcher.Matcher
import com.coda.situlearner.infra.subkit.matcher.ja.KanaUtils.isKanji
import com.coda.situlearner.infra.subkit.matcher.weightedLevenshteinSimilarity

internal class MatcherJa : Matcher {

    companion object {
        // estimate of information density when comparing kanji to kana
        private const val DELETION_KANJI = 2.5
        private const val DELETION_KANA = 1.0
        private const val INSERTION_KANJI = 2.5
        private const val INSERTION_KANA = 1.0
        private const val SUBSTITUTION_MATCH = 0.0
        private const val SUBSTITUTION_DAKUTEN_VARIANT = 0.2
        private const val SUBSTITUTION_SAME_ROW = 0.4
        private const val SUBSTITUTION_SAME_COL = 0.7
        private const val SUBSTITUTION_MISMATCH_ONLY_KANA = 1.0
        private const val SUBSTITUTION_MISMATCH_WITH_KANJI = 2.5
    }

    override fun matchLemma(
        query: String,
        target: String
    ): Double {
        val q = KanaUtils.normalizeLemma(query)
        val t = KanaUtils.normalizeLemma(target)
        return weightedLevenshteinSimilarity(
            query = q,
            target = t,
            deletion = { if (it.isKanji()) DELETION_KANJI else DELETION_KANA },
            insertion = { if (it.isKanji()) INSERTION_KANJI else INSERTION_KANA },
            substitution = { c1, c2 -> getSubstitutionCost(c1, c2) }
        )
    }

    override fun matchPronunciation(
        query: String,
        target: String
    ): Double {
        val q = KanaUtils.normalizePronunciation(query)
        val t = KanaUtils.normalizePronunciation(target)
        return weightedLevenshteinSimilarity(
            query = q,
            target = t,
            substitution = { c1, c2 -> getSubstitutionCost(c1, c2) }
        )
    }

    private fun getSubstitutionCost(c1: Char, c2: Char): Double {
        if (c1 == c2) return SUBSTITUTION_MATCH
        if (c1.isKanji() || c2.isKanji()) return SUBSTITUTION_MISMATCH_WITH_KANJI
        if (KanaUtils.getBaseChar(c1) == KanaUtils.getBaseChar(c2)) return SUBSTITUTION_DAKUTEN_VARIANT
        if (KanaUtils.isSameRow(c1, c2)) return SUBSTITUTION_SAME_ROW
        if (KanaUtils.isSameCol(c1, c2)) return SUBSTITUTION_SAME_COL
        return SUBSTITUTION_MISMATCH_ONLY_KANA
    }
}