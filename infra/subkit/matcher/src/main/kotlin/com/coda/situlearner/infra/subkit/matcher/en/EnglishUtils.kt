package com.coda.situlearner.infra.subkit.matcher.en

internal object EnglishUtils {

    private val IPA_CHARS = setOf(
        'p', 'b', 't', 'd', 'k', 'g', 'f', 'v', 's', 'z', 'ʃ', 'ʒ', 'θ', 'ð', 'h',
        'm', 'n', 'ŋ', 'r', 'l', 'j', 'w',
        'i', 'ɪ', 'e', 'ɛ', 'æ', 'a', 'ɑ', 'ʌ', 'ə', 'u', 'ʊ', 'o', 'ɔ', 'ɒ'
    )

    private val VOICED_PAIRS = mapOf(
        'p' to 0, 'b' to 0,
        't' to 1, 'd' to 1,
        'k' to 2, 'g' to 2,
        'f' to 3, 'v' to 3,
        's' to 4, 'z' to 4,
        'ʃ' to 5, 'ʒ' to 5,
        'θ' to 6, 'ð' to 6
    )

    private val VOWEL_GROUPS = mapOf(
        'i' to 0, 'ɪ' to 0,
        'e' to 1, 'ɛ' to 1, 'æ' to 1,
        'u' to 2, 'ʊ' to 2,
        'ɑ' to 3, 'ɒ' to 3, 'ɔ' to 3,
        'ə' to 4, 'ʌ' to 4
    )

    private val CONSONANT_CLASS = mapOf(
        // stops
        'p' to 0, 'b' to 0,
        't' to 0, 'd' to 0,
        'k' to 0, 'g' to 0,

        // fricatives
        'f' to 1, 'v' to 1,
        's' to 1, 'z' to 1,
        'ʃ' to 1, 'ʒ' to 1,
        'θ' to 1, 'ð' to 1,
        'h' to 1,

        // nasals
        'm' to 2, 'n' to 2, 'ŋ' to 2,

        // liquids
        'l' to 3, 'r' to 3,

        // glides
        'w' to 4, 'j' to 4
    )


    fun normalizeLemma(s: String) = buildString {
        for (ch in s.lowercase()) {
            if (ch in 'a'..'z' || ch == '-') {
                append(ch)
            }
        }
    }

    fun normalizePronunciation(s: String) = buildString {
        for (ch in s) {
            if (ch in IPA_CHARS) {
                append(ch)
            }
        }
    }

    fun isVoicedPair(c1: Char, c2: Char): Boolean {
        val v1 = VOICED_PAIRS[c1] ?: -1
        val v2 = VOICED_PAIRS[c2] ?: -2
        return v1 == v2
    }

    fun isVowelGroup(c1: Char, c2: Char): Boolean {
        val v1 = VOWEL_GROUPS[c1] ?: -1
        val v2 = VOWEL_GROUPS[c2] ?: -2
        return v1 == v2
    }

    fun isConsonantClass(c1: Char, c2: Char): Boolean {
        val v1 = CONSONANT_CLASS[c1] ?: -1
        val v2 = CONSONANT_CLASS[c2] ?: -2
        return v1 == v2
    }

    fun Char.isVowel(): Boolean = VOWEL_GROUPS.containsKey(this)
}