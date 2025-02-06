package com.coda.situlearner.core.model.data

enum class PartOfSpeech(val level: Int) {
    Unknown(100),
    Noun(0),
    Verb(1),
    Adjective(2),
    Adverb(3);
}