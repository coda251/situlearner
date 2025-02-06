package com.coda.situlearner.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WordWithContextsEntity(
    @Embedded val word: WordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId",
        entity = WordContextEntityView::class,
    )
    val contexts: List<WordContextEntityView>,
)