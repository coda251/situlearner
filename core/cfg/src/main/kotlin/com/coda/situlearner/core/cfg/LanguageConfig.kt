package com.coda.situlearner.core.cfg

import com.coda.situlearner.core.model.data.Language

object LanguageConfig {
    val targetLanguage = Language.Chinese

    val sourceLanguages = listOf(
        Language.English,
        Language.Japanese
    )
}