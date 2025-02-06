package com.coda.situlearner.core.database

import com.coda.situlearner.core.database.entity.MediaCollectionEntity
import com.coda.situlearner.core.database.entity.MediaFileEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.model.Audio
import com.coda.situlearner.core.database.model.Unknown_Language
import com.coda.situlearner.core.database.model.Unknown_PartOfSpeech
import com.coda.situlearner.core.database.model.Unset
import kotlinx.datetime.Clock
import org.jetbrains.annotations.TestOnly

@TestOnly
internal val mediaCollections = listOf(
    MediaCollectionEntity(
        id = "0",
        name = "collection_0",
        url = "",
        coverUrl = null
    )
)

@TestOnly
internal val mediaFiles = listOf(
    MediaFileEntity(
        id = "0",
        collectionId = "0",
        name = "file_0",
        url = "file_url_0",
        subtitleUrl = null,
        mediaType = Audio,
        durationInMs = null
    )
)

@TestOnly
internal val words = listOf(
    WordEntity(
        id = "0",
        word = "word_0",
        language = Unknown_Language,
        dictionaryName = null,
        pronunciation = null,
        meanings = mapOf(
            "noun" to "this is a noun",
            "v." to "this is a verb"
        ),
        lastViewedDate = Clock.System.now(),
        proficiency = Unset
    )
)

@TestOnly
internal val wordContexts = listOf(
    WordContextEntity(
        id = "0",
        wordId = "0",
        mediaId = "0",
        createdDate = Clock.System.now(),
        partOfSpeech = Unknown_PartOfSpeech,
        subtitleStartTimeInMs = 0L,
        subtitleEndTimeInMs = 1L,
        subtitleSourceText = "",
        subtitleTargetText = null,
        wordStartIndex = 0,
        wordEndIndex = 4
    )
)