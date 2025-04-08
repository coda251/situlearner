package com.coda.situlearner.core.model.data.mapper

import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaCollectionWithFiles
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.core.model.data.WordContextView

fun Pair<MediaCollection, MediaFile>.asPlaylistItem(): PlaylistItem =
    PlaylistItem(
        id = second.id,
        name = second.name,
        collectionName = first.name,
        mediaUrl = second.url,
        mediaType = second.mediaType,
        subtitleUrl = second.subtitleUrl,
        thumbnailUrl = first.coverImageUrl,
        durationInMs = second.durationInMs,
        lastPositionInMs = null
    )

fun WordContextView.asPlaylistItem(): PlaylistItem? {
    val file = mediaFile
    val collection = mediaCollection
    return if (file != null && collection != null) {
        Pair(collection, file).asPlaylistItem().copy(
            id = wordContext.id,
            clipInMs = Pair(
                first = wordContext.subtitleStartTimeInMs,
                second = wordContext.subtitleEndTimeInMs
            )
        )
    } else {
        null
    }
}

fun MediaCollectionWithFiles.asPlaylist(): List<PlaylistItem> =
    buildList {
        files.forEach {
            add((collection to it).asPlaylistItem())
        }
    }