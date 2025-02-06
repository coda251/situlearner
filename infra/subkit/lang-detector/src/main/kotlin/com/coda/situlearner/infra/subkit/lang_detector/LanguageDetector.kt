package com.coda.situlearner.infra.subkit.lang_detector

import com.coda.situlearner.core.model.data.Language

interface LanguageDetector {

    suspend fun detect(text: String): Language
}