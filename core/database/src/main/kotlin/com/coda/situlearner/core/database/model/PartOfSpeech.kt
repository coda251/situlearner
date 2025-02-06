package com.coda.situlearner.core.database.model

@JvmInline
value class PartOfSpeech(val value: Int)

val Unknown_PartOfSpeech = PartOfSpeech(0)
val Noun = PartOfSpeech(1)
val Verb = PartOfSpeech(2)
val Adjective = PartOfSpeech(3)
val Adverb = PartOfSpeech(4)