package com.coda.situlearner.infra.subkit.processor

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.SubtitleFileContent
import com.coda.situlearner.infra.subkit.lang_detector.LanguageDetector
import com.coda.situlearner.infra.subkit.tokenizer.Tokenizer

interface Processor {

    suspend fun load(filePath: String): SubtitleFileContent?

    suspend fun process(
        filePath: String,
        sourceLanguage: Language,
        targetLanguage: Language,
        tokenizer: Tokenizer,
        detector: LanguageDetector,
    ): SubtitleFileContent?
}