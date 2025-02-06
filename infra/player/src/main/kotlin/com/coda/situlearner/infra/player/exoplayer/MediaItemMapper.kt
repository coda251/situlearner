package com.coda.situlearner.infra.player.exoplayer

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.data.PlaylistItem
import com.coda.situlearner.infra.player.PlayerState

internal object MediaMetadataExtraKey {
    internal const val MEDIA_URL = "mediaUrl"
    internal const val SUBTITLE_URL = "subtitleUrl"
    internal const val THUMBNAIL_URL = "thumbnailUrl"
    internal const val MEDIA_TYPE = "mediaType"
    internal const val DURATION = "duration"
    internal const val LAST_PLAY_POSITION = "lastPlayPosition"
}

internal fun PlaylistItem.asMediaItem() = MediaItem.Builder().apply {
    setMediaId(id)
    setUri(mediaUrl)
    setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(name)
            .setAlbumTitle(collectionName)
            .setExtras(
                Bundle().apply {
                    putString(MediaMetadataExtraKey.MEDIA_URL, mediaUrl)
                    putString(MediaMetadataExtraKey.SUBTITLE_URL, subtitleUrl)
                    putString(MediaMetadataExtraKey.THUMBNAIL_URL, thumbnailUrl)
                    putString(MediaMetadataExtraKey.MEDIA_TYPE, mediaType.toString())
                    putLong(MediaMetadataExtraKey.DURATION, durationInMs ?: PlayerState.TIME_UNSET)
                    putLong(
                        MediaMetadataExtraKey.LAST_PLAY_POSITION,
                        lastPositionInMs ?: PlayerState.TIME_UNSET
                    )
                }
            )
            .build()
    )
}.build()

internal fun MediaItem.asPlaylistItem() = PlaylistItem(
    id = mediaId,
    mediaUrl = getString(MediaMetadataExtraKey.MEDIA_URL)!!,
    name = mediaMetadata.title!!.toString(),
    collectionName = mediaMetadata.albumTitle!!.toString(),
    subtitleUrl = getString(MediaMetadataExtraKey.SUBTITLE_URL),
    thumbnailUrl = getString(MediaMetadataExtraKey.THUMBNAIL_URL),
    mediaType = MediaType.valueOf(getString(MediaMetadataExtraKey.MEDIA_TYPE)!!),
    durationInMs = getLong(MediaMetadataExtraKey.DURATION).takeIf { it != PlayerState.TIME_UNSET },
    lastPositionInMs = getLong(MediaMetadataExtraKey.LAST_PLAY_POSITION).takeIf { it != PlayerState.TIME_UNSET },
)

internal val MediaItem.startPlayingPosition: Long
    get() {
        val position = getLong(MediaMetadataExtraKey.LAST_PLAY_POSITION)
        return if (position == PlayerState.TIME_UNSET) 0 else position
    }

private fun MediaItem.getString(key: String): String? = mediaMetadata.extras!!.getString(key)

private fun MediaItem.getLong(key: String): Long =
    mediaMetadata.extras!!.getLong(key, PlayerState.TIME_UNSET)

internal fun MediaItem.putLong(key: String, value: Long) =
    mediaMetadata.extras!!.putLong(key, value)