package com.coda.situlearner.core.model.infra

import com.coda.situlearner.core.model.data.Language
import kotlinx.serialization.Serializable

@Serializable
data class SubtitleFileContent(
    val subtitles: List<Subtitle>,
    val sourceLanguage: Language = Language.Unknown,
    val targetLanguage: Language = Language.Unknown,
)