package com.coda.situlearner.core.model.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class WordContext(
    val id: String,
    val wordId: String,
    val mediaId: String?,
    val createdDate: Instant = Clock.System.now(),
    val partOfSpeech: PartOfSpeech,
    val subtitleStartTimeInMs: Long,
    val subtitleEndTimeInMs: Long,
    val subtitleSourceText: String,
    val subtitleTargetText: String?,
    val wordStartIndex: Int,
    val wordEndIndex: Int,
)