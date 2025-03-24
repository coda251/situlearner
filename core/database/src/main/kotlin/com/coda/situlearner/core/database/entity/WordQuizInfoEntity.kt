package com.coda.situlearner.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("wordId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WordQuizInfoEntity(
    @PrimaryKey val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant,
)