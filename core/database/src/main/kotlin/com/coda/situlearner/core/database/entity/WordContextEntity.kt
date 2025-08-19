package com.coda.situlearner.core.database.entity

import androidx.room.ColumnInfo
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
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MediaFileEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("mediaId"),
            onDelete = ForeignKey.SET_NULL,
        )
    ]
)
data class WordContextEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val wordId: String,
    @ColumnInfo(index = true) val mediaId: String?,
    val createdDate: Instant,
    val subtitleStartTimeInMs: Long,
    val subtitleEndTimeInMs: Long,
    val subtitleSourceText: String,
    val subtitleTargetText: String?,
    val wordStartIndex: Int,
    val wordEndIndex: Int,
)