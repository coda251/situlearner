package com.coda.situlearner.core.cfg

import com.coda.situlearner.core.model.data.Language

object AppConfig {
    // language config
    val targetLanguage = Language.Chinese

    /**
     * Only languages implemented in infra.subkit (detector, tokenizer, translator)
     * could be added into sourceLanguages.
     */
    val sourceLanguages = listOf(Language.English, Language.Japanese)

    /**
     * Should not be [Language.Unknown].
     */
    val defaultSourceLanguage = Language.English

    // word config
    const val DEFAULT_QUIZ_WORD_COUNT = 15u
    const val DEFAULT_RECOMMENDED_WORD_COUNT = 20u
}