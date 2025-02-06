package com.coda.situlearner.core.model.domain

import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts

sealed class WordCategory(
    open val wordWithContextsList: List<WordWithContexts>,
) {
    val wordCount: Int
        get() = wordWithContextsList.size

    val wordContexts: List<WordContextView> by lazy {
        wordWithContextsList.flatMap { it.contexts }
    }
}

data class WordViewedDateCategory(
    val timeFrame: TimeFrame,
    override val wordWithContextsList: List<WordWithContexts>,
) : WordCategory(wordWithContextsList)

data class WordProficiencyCategory(
    val proficiency: WordProficiency,
    override val wordWithContextsList: List<WordWithContexts>,
) : WordCategory(wordWithContextsList)

data class WordPOSCategory(
    val partOfSpeech: PartOfSpeech,
    override val wordWithContextsList: List<WordWithContexts>,
) : WordCategory(wordWithContextsList)

data class WordMediaCategory(
    val collection: MediaCollection,
    override val wordWithContextsList: List<WordWithContexts>,
) : WordCategory(wordWithContextsList)