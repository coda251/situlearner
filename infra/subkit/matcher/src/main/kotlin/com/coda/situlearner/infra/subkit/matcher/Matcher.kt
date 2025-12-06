package com.coda.situlearner.infra.subkit.matcher

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.infra.subkit.matcher.en.MatcherEn
import com.coda.situlearner.infra.subkit.matcher.ja.MatcherJa

interface Matcher {
    companion object {
        fun getMatcher(language: Language) = when (language) {
            Language.Japanese -> MatcherJa()
            Language.English -> MatcherEn()
            else -> throw IllegalArgumentException("Unsupported language: $language")
        }
    }

    fun matchLemma(query: String, target: String): Double
    fun matchPronunciation(query: String, target: String): Double
}