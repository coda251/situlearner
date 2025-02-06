package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.database.model.Adjective
import com.coda.situlearner.core.database.model.Adverb
import com.coda.situlearner.core.database.model.Noun
import com.coda.situlearner.core.database.model.Unknown_PartOfSpeech
import com.coda.situlearner.core.database.model.Verb
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.database.model.PartOfSpeech as PartOfSpeechValue

internal fun PartOfSpeechValue.asExternalModel() = when (this) {
    Noun -> PartOfSpeech.Noun
    Verb -> PartOfSpeech.Verb
    Adjective -> PartOfSpeech.Adjective
    Adverb -> PartOfSpeech.Adverb
    else -> PartOfSpeech.Unknown
}

internal fun PartOfSpeech.asValue() = when (this) {
    PartOfSpeech.Noun -> Noun
    PartOfSpeech.Verb -> Verb
    PartOfSpeech.Adjective -> Adjective
    PartOfSpeech.Adverb -> Adverb
    PartOfSpeech.Unknown -> Unknown_PartOfSpeech
}