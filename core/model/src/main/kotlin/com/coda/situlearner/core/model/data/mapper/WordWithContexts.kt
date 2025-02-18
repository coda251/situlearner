package com.coda.situlearner.core.model.data.mapper

import com.coda.situlearner.core.model.data.WordCategoryType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.domain.TimeFrame
import com.coda.situlearner.core.model.domain.WordCategoryList
import com.coda.situlearner.core.model.domain.WordMediaCategory
import com.coda.situlearner.core.model.domain.WordPOSCategory
import com.coda.situlearner.core.model.domain.WordProficiencyCategory
import com.coda.situlearner.core.model.domain.WordViewedDateCategory
import kotlinx.datetime.Instant

fun WordWithContexts.asPlaylistItem() = contexts.takeIf { it.isNotEmpty() }?.random()?.let {
    val file = it.mediaFile
    val collection = it.mediaCollection
    if (file != null && collection != null) {
        Pair(collection, file).asPlaylistItem().copy(
            id = it.wordContext.id,
            clipInMs = Pair(
                first = it.wordContext.subtitleStartTimeInMs,
                second = it.wordContext.subtitleEndTimeInMs
            )
        )
    } else {
        null
    }
}

fun List<WordWithContexts>.toWordCategoryList(
    categoryType: WordCategoryType,
    timeFrameProvider: (Instant?) -> TimeFrame
): WordCategoryList = WordCategoryList(
    categoryType = categoryType,
    categories = when (categoryType) {
        WordCategoryType.LastViewedDate -> toWordViewedDateCategoryList(timeFrameProvider)
        WordCategoryType.Proficiency -> toWordProficiencyCategoryList()
        WordCategoryType.Media -> toWordMediaCategoryList()
        WordCategoryType.PartOfSpeech -> toWordPOSCategoryList()
    }
)

internal fun List<WordWithContexts>.toWordViewedDateCategoryList(timeFrameProvider: (Instant?) -> TimeFrame) =
    this.groupBy { wordWithContexts ->
        timeFrameProvider(wordWithContexts.word.lastViewedDate)
    }.entries.map { entry ->
        WordViewedDateCategory(
            timeFrame = entry.key,
            wordWithContextsList = entry.value
        )
    }.sortedBy { it.timeFrame.level }

internal fun List<WordWithContexts>.toWordProficiencyCategoryList() =
    this.groupBy { it.word.proficiency }.entries.map { entry ->
        WordProficiencyCategory(
            proficiency = entry.key,
            wordWithContextsList = entry.value

        )
    }.sortedBy { it.proficiency.level }

internal fun List<WordWithContexts>.toWordMediaCategoryList(): List<WordMediaCategory> {
    val idToWord = this.map { it.word }.associateBy { it.id }
    return this.flatMap { it.contexts }.groupBy { it.mediaCollection }.entries.mapNotNull { entry ->
        val mediaCollection = entry.key
        val wordContextsView = entry.value
        mediaCollection?.let { collection ->
            WordMediaCategory(
                collection = collection,
                wordWithContextsList = wordContextsView.groupBy { it.wordContext.wordId }.entries.mapNotNull {
                    idToWord[it.key]?.let { word ->
                        WordWithContexts(
                            word = word,
                            contexts = it.value
                        )
                    }
                }
            )
        }
    }.sortedBy { it.collection.name }
}

internal fun List<WordWithContexts>.toWordPOSCategoryList(): List<WordPOSCategory> {
    val idToWord = this.map { it.word }.associateBy { it.id }
    return this.flatMap { it.contexts }
        .groupBy { it.wordContext.partOfSpeech }.entries.map { entry ->
            WordPOSCategory(
                partOfSpeech = entry.key,
                wordWithContextsList = entry.value.groupBy { it.wordContext.wordId }.entries.mapNotNull {
                    idToWord[it.key]?.let { word ->
                        WordWithContexts(
                            word = word,
                            contexts = it.value
                        )
                    }
                }
            )
        }.sortedBy { it.partOfSpeech.level }
}
