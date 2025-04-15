package com.coda.situlearner.core.model.data.mapper

import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.infra.WordInfo

fun Word.asWordInfo() = WordInfo(
    word = word,
    dictionaryName = dictionaryName,
    pronunciation = pronunciation,
    meanings = meanings
)