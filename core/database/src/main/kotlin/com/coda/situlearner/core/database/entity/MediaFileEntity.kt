package com.coda.situlearner.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.coda.situlearner.core.database.model.MediaType

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MediaCollectionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("collectionId"),
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class MediaFileEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val collectionId: String,
    val name: String,
    val url: String,
    val subtitleUrl: String?,
    // metadata
    val mediaType: MediaType,
    val durationInMs: Long?,
)