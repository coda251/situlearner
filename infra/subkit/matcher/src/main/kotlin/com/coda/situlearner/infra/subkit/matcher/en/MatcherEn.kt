package com.coda.situlearner.infra.subkit.matcher.en

import com.coda.situlearner.infra.subkit.matcher.Matcher
import com.coda.situlearner.infra.subkit.matcher.en.EnglishUtils.isVowel
import com.coda.situlearner.infra.subkit.matcher.weightedLevenshteinSimilarity

internal class MatcherEn : Matcher {

    companion object {
        private const val SUBSTITUTION_MATCH = 0.0
        private const val SUBSTITUTION_VOICED_PAIR = 0.2
        private const val SUBSTITUTION_VOWEL_GROUP = 0.4
        private const val SUBSTITUTION_CONSONANT_CLASS = 0.5
        private const val SUBSTITUTION_VOWEL = 0.7
        private const val SUBSTITUTION_MISMATCH = 1.0
    }

    override fun matchLemma(query: String, target: String): Double {
        val q = EnglishUtils.normalizeLemma(query)
        val t = EnglishUtils.normalizeLemma(target)
        return weightedLevenshteinSimilarity(q, t)
    }

    override fun matchPronunciation(query: String, target: String): Double {
        val q = EnglishUtils.normalizePronunciation(query)
        val t = EnglishUtils.normalizePronunciation(target)
        return weightedLevenshteinSimilarity(
            query = q,
            target = t,
            substitution = { c1, c2 -> 1 - getSubstitutionCost(c1, c2) }
        )
    }

    private fun getSubstitutionCost(c1: Char, c2: Char): Double {
        if (c1 == c2) return SUBSTITUTION_MATCH
        if (EnglishUtils.isVoicedPair(c1, c2)) return SUBSTITUTION_VOICED_PAIR
        if (c1.isVowel() && c2.isVowel()) {
            return if (EnglishUtils.isVowelGroup(c1, c2)) SUBSTITUTION_VOWEL_GROUP
            else SUBSTITUTION_VOWEL
        }
        if (EnglishUtils.isConsonantClass(c1, c2)) return SUBSTITUTION_CONSONANT_CLASS
        return SUBSTITUTION_MISMATCH
    }
}