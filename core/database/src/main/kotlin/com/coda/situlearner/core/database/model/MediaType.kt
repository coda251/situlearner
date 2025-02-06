package com.coda.situlearner.core.database.model

@JvmInline
value class MediaType(val value: Int)

val Audio = MediaType(0)
val Video = MediaType(1)