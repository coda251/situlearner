package com.coda.situlearner.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MediaCollectionWithFilesEntity(
    @Embedded val collection: MediaCollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId",
    )
    val files: List<MediaFileEntity>,
)