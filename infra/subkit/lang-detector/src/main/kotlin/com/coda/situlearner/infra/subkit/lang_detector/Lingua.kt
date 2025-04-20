package com.coda.situlearner.infra.subkit.lang_detector

import com.coda.situlearner.core.model.data.Language
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.github.pemistahl.lingua.api.Language as LinguaLanguage
import com.github.pemistahl.lingua.api.LanguageDetector as LinguaDetector

internal class Lingua private constructor(
    private val detector: LinguaDetector
) : LanguageDetector {

    companion object {
        suspend fun build() = withContext(Dispatchers.IO) {
            Lingua(
                LanguageDetectorBuilder.fromLanguages(
                    *Language.validLanguages.map { it.asLinguaLanguage() }.toTypedArray()
                ).build()
            )
        }
    }

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