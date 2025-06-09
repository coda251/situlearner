package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.database.entity.MeaningQuizStatsEntity
import com.coda.situlearner.core.database.entity.TranslationQuizStatsEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordContextEntityView
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts

internal fun WordEntity.asExternalModel() = Word(
    id = id,
    word = word,
    language = language.asExternalModel(),
    dictionaryName = dictionaryName,
    pronunciation = pronunciation,
    meanings = meanings?.let { meaningsMap ->
        meaningsMap.entries.map {
            WordMeaning(it.key, it.value)
        }
    } ?: emptyList(),
    lastViewedDate = lastViewedDate,
    createdDate = createdDate,
    meaningProficiency = meaningProficiency.asExternalModel(),
    translationProficiency = translationProficiency?.asExternalModel() ?: WordProficiency.Unset
)

internal fun WordContextEntity.asExternalModel() = WordContext(
    id = id,
    wordId = wordId,
    mediaId = mediaId,
    createdDate = createdDate,
    subtitleStartTimeInMs = subtitleStartTimeInMs,
    subtitleEndTimeInMs = subtitleEndTimeInMs,
    subtitleSourceText = subtitleSourceText,
    subtitleTargetText = subtitleTargetText,
    wordStartIndex = wordStartIndex,
    wordEndIndex = wordEndIndex,
)

internal fun WordContextEntityView.asExternalModel() = WordContextView(
    wordContext = wordContext.asExternalModel(),
    mediaFile = mediaFile?.asExternalModel(),
    mediaCollection = mediaCollection?.asExternalModel()
)

internal fun WordWithContextsEntity.asExternalModel() = WordWithContexts(
    word = word.asExternalModel(),
    contexts = contexts.map(WordContextEntityView::asExternalModel)
)

internal fun Word.asEntity() = WordEntity(
    id = id,
    word = word,
    language = language.asValue(),
    dictionaryName = dictionaryName,
    pronunciation = pronunciation,
    meanings = meanings.let {
        buildMap {
            it.forEach {
                this[it.partOfSpeechTag] = it.definition
            }
        }
    },
    lastViewedDate = lastViewedDate,
    createdDate = createdDate,
    meaningProficiency = meaningProficiency.asValue(),
    translationProficiency = translationProficiency.asValue()
)

internal fun WordContext.asEntity() = WordContextEntity(
    id = id,
    wordId = wordId,
    mediaId = mediaId,
    createdDate = createdDate,
    subtitleStartTimeInMs = subtitleStartTimeInMs,
    subtitleEndTimeInMs = subtitleEndTimeInMs,
    subtitleSourceText = subtitleSourceText,
    subtitleTargetText = subtitleTargetText,
    wordStartIndex = wordStartIndex,
    wordEndIndex = wordEndIndex,
)

internal fun MeaningQuizStats.asEntity() = MeaningQuizStatsEntity(
    wordId = wordId,
    easeFactor = easeFactor,
    intervalDays = intervalDays,
    nextQuizDate = nextQuizDate
)

internal fun MeaningQuizStatsEntity.asExternalModel() = MeaningQuizStats(
    wordId = wordId,
    easeFactor = easeFactor,
    intervalDays = intervalDays,
    nextQuizDate = nextQuizDate
)

internal fun TranslationQuizStatsEntity.asExternalModel() = TranslationQuizStats(
    wordId = wordId,
    easeFactor = easeFactor,
    intervalDays = intervalDays,
    nextQuizDate = nextQuizDate,
    lastQuestion = lastQuestion,
    userAnswer = userAnswer
)

internal fun TranslationQuizStats.asEntity() = TranslationQuizStatsEntity(
    wordId = wordId,
    easeFactor = easeFactor,
    intervalDays = intervalDays,
    nextQuizDate = nextQuizDate,
    lastQuestion = lastQuestion,
    userAnswer = userAnswer
)