package com.coda.situlearner.core.database.model

@JvmInline
value class WordProficiency(val value: Int)

val Unset = WordProficiency(0)
val Beginner = WordProficiency(1)
val Intermediate = WordProficiency(2)
val Proficient = WordProficiency(3)