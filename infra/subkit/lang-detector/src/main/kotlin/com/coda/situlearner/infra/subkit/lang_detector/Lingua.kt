package com.coda.situlearner.infra.subkit.lang_detector

import com.coda.situlearner.core.model.data.Language
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import com.github.pemistahl.lingua.api.Language as LinguaLanguage

class Lingua : LanguageDetector {

    private val detector = LanguageDetectorBuilder.fromLanguages(
        *Language.validLanguages.map { it.asLinguaLanguage() }.toTypedArray()
    ).build()

    override suspend fun detect(text: String): Language {
        return detector.detectLanguageOf(text).asLanguage()
    }
}

private fun Language.asLinguaLanguage() = when (this) {
    Language.Chinese -> LinguaLanguage.CHINESE
    Language.English -> LinguaLanguage.ENGLISH
    Language.Japanese -> LinguaLanguage.JAPANESE
    Language.Unknown -> LinguaLanguage.UNKNOWN
}

private fun LinguaLanguage.asLanguage() = when (this) {
    LinguaLanguage.ENGLISH -> Language.English
    LinguaLanguage.JAPANESE -> Language.Japanese
    LinguaLanguage.CHINESE -> Language.Chinese
    else -> Language.Unknown
}