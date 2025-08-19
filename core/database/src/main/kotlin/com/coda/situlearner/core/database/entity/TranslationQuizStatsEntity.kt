package com.coda.situlearner.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.time.Instant

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
data class TranslationQuizStatsEntity(
    @PrimaryKey val wordId: String,
    val easeFactor: Double,
    val intervalDays: Int,
    val nextQuizDate: Instant,
    val lastQuestion: String,
    val userAnswer: String,
)