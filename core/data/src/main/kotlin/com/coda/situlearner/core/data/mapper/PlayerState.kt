package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.MediaTypeProto
import com.coda.situlearner.core.datastore.PlayerStateProto
import com.coda.situlearner.core.datastore.PlaylistItemProto
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.PlayerStateData
import com.coda.situlearner.core.model.data.Playlist
import com.coda.situlearner.core.model.data.PlaylistItem

private const val POSITION_UNSET = -1L

private const val URL_UNSET = ""

internal fun PlayerStateProto.asExternalModel() = PlayerStateData(
    repeatMode = repeatMode.asExternalModel(),
    positionInMs = positionInMs,
    playlist = Playlist(
        items = itemsList.map(PlaylistItemProto::asExternalModel),
        currentIndex = currentIndex
    )
)

internal fun PlaylistItemProto.asExternalModel() = PlaylistItem(
    id = id,
    name = name,
    collectionName = collectionName,
    mediaUrl = mediaUrl,
    subtitleUrl = if (subtitleUrl == URL_UNSET) null else subtitleUrl,
    thumbnailUrl = if (thumbnailUrl == URL_UNSET) null else thumbnailUrl,
    mediaType = mediaType.asExternalModel(),
    durationInMs = if (durationInMs == POSITION_UNSET) null else durationInMs,
    lastPositionInMs = if (lastPositionInMs == POSITION_UNSET) null else lastPositionInMs
)

internal fun PlaylistItem.asProto(): PlaylistItemProto = PlaylistItemProto.newBuilder().apply {
    id = this@asProto.id
    name = this@asProto.name
    collectionName = this@asProto.collectionName
    mediaUrl = this@asProto.mediaUrl
    subtitleUrl = this@asProto.subtitleUrl ?: URL_UNSET
    thumbnailUrl = this@asProto.thumbnailUrl ?: URL_UNSET
    mediaType = this@asProto.mediaType.asProto()
    durationInMs = this@asProto.durationInMs ?: POSITION_UNSET
    lastPositionInMs = this@asProto.lastPositionInMs ?: POSITION_UNSET
}.build()

internal fun MediaType.asProto() = when (this) {
    MediaType.Audio -> MediaTypeProto.MEDIA_TYPE_AUDIO
    MediaType.Video -> MediaTypeProto.MEDIA_TYPE_VIDEO
}

internal fun MediaTypeProto.asExternalModel() = when (this) {
    MediaTypeProto.MEDIA_TYPE_AUDIO, MediaTypeProto.UNRECOGNIZED -> MediaType.Audio
    MediaTypeProto.MEDIA_TYPE_VIDEO -> MediaType.Video
}