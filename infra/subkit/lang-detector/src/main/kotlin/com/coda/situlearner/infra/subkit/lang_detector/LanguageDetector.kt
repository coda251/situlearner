package com.coda.situlearner.infra.subkit.lang_detector

import com.coda.situlearner.core.model.data.Language

interface LanguageDetector {

    companion object {
        suspend fun getInstance(): LanguageDetector = Lingua.build()
    }

    suspend fun detect(text: String): Language
}