package com.coda.situlearner.core.database.model

@JvmInline
value class Language(val value: Int)

// it's an issue that we can not hold these constants in companion object in value class,
// so we have to make them as top level,
// see https://github.com/google/ksp/issues/1700
val Unknown_Language = Language(0)
val Chinese = Language(1)
val English = Language(2)
val Japanese = Language(3)