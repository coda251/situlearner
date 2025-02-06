package com.coda.situlearner.core.database.entity

import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    viewName = "WordContextEntityView",
    value = """
        SELECT
            wc.*,
            mf.id AS media_file_id,
            mf.collectionId AS media_file_collectionId,
            mf.name AS media_file_name,
            mf.url AS media_file_url,
            mf.subtitleUrl AS media_file_subtitleUrl,
            mf.mediaType AS media_file_mediaType,
            mf.durationInMs AS media_file_durationInMs,
            mg.id AS media_collection_id,
            mg.name AS media_collection_name,
            mg.url AS media_collection_url,
            mg.coverUrl AS media_collection_coverUrl
        FROM WordContextEntity wc
        LEFT JOIN MediaFileEntity mf ON wc.mediaId = mf.id
        LEFT JOIN MediaCollectionEntity mg ON mf.collectionId = mg.id
    """
)
data class WordContextEntityView(
    @Embedded val wordContext: WordContextEntity,
    @Embedded(prefix = "media_file_") val mediaFile: MediaFileEntity?,
    @Embedded(prefix = "media_collection_") val mediaCollection: MediaCollectionEntity?,
)