package com.coda.situlearner.core.testing.data

import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.MediaFile
import com.coda.situlearner.core.model.data.MediaType

val mediaFilesTestData = listOf(
    MediaFile(
        id = "0",
        collectionId = "0",
        name = "Example S01E01",
        url = "",
        subtitleUrl = "file:///storage/emulated/0/Download/fake.stk",
        mediaType = MediaType.Video,
        durationInMs = 300000L
    ),
    MediaFile(
        id = "1",
        collectionId = "0",
        name = "",
        url = "",
        mediaType = MediaType.Audio,
        subtitleUrl = null,
        durationInMs = null,
    ),
    MediaFile(
        id = "2",
        collectionId = "1",
        name = "A file with really really really really really really long name",
        url = "",
        subtitleUrl = null,
        mediaType = MediaType.Audio,
        durationInMs = 1440000L,
    )
)

val mediaCollectionsTestData = listOf(

    MediaCollection(
        id = "0",
        name = "Example",
        url = ""
        // a coverUrl may cause error in preview on real device
        // coverUrl = "https://developer.android.google.cn/images/logos/android.svg",
    ),
    MediaCollection(
        id = "1",
        name = "A collection with really really really really really really long name",
        url = "",
        coverImageUrl = null
    ),
    MediaCollection(
        id = "2",
        name = "",
        url = "",
        coverImageUrl = null,
    )
)