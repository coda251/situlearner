package com.coda.situlearner.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MediaCollectionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val coverUrl: String?,
)