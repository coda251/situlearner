package com.coda.situlearner.core.model.data

data class Playlist(
    val items: List<PlaylistItem> = emptyList(),
    val currentIndex: Int = -1
) : List<PlaylistItem> by items {

    val currentItem: PlaylistItem?
        get() = getOrNull(currentIndex)
}
