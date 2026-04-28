package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.database.entity.MeaningQuizStatsEntity
import com.coda.situlearner.core.database.entity.TranslationQuizStatsEntity
import com.coda.situlearner.core.database.entity.WordContextEntity
import com.coda.situlearner.core.database.entity.WordContextEntityView
import com.coda.situlearner.core.database.entity.WordEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.database.model.Beginner
import com.coda.situlearner.core.database.model.Chinese
import com.coda.situlearner.core.database.model.English
import com.coda.situlearner.core.database.model.Intermediate
import com.coda.situlearner.core.database.model.Japanese
import com.coda.situlearner.core.database.model.Proficient
import com.coda.situlearner.core.database.model.Unknown_Language
import com.coda.situlearner.core.database.model.Unset
import com.coda.situlearner.core.datastore.LanguageProto
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.MeaningQuizStats
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordContext
import com.coda.situlearner.core.model.data.WordContextView
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.database.model.Language as LanguageValue
import com.coda.situlearner.core.database.model.WordProficiency as WordProficiencyValue

internal fun LanguageProto.asExternalModel() = when (this) {
    // it should be specified when used in user preference in the domain layer
    LanguageProto.LANGUAGE_UNSPECIFIED, LanguageProto.UNRECOGNIZED -> Language.Unknown
    LanguageProto.LANGUAGE_CHINESE -> Language.Chinese
    LanguageProto.LANGUAGE_ENGLISH -> Language.English
    LanguageProto.LANGUAGE_JAPANESE -> Language.Japanese
}

internal fun Language.asProto() = when (this) {
    Language.Unknown -> LanguageProto.LANGUAGE_UNSPECIFIED
    Language.Chinese -> LanguageProto.LANGUAGE_CHINESE
    Language.English -> LanguageProto.LANGUAGE_ENGLISH
    Language.Japanese -> LanguageProto.LANGUAGE_JAPANESE
}

internal fun LanguageValue.asExternalModel() = when (this) {
    Chinese -> Language.Chinese
    English -> Language.English
    Japanese -> Language.Japanese
    else -> Language.Unknown
}

internal fun Language.asValue() = when (this) {
    Language.Chinese -> Chinese
    Language.English -> English
    Language.Japanese -> Japanese
    Language.Unknown -> Unknown_Language
}

internal fun WordProficiencyValue.asExternalModel() = when (this) {
    Beginner -> WordProficiency.Beginner
    Intermediate -> WordProficiency.Intermediate
    Proficient -> WordProficiency.Proficient
    else -> WordProficiency.Unset
}

internal fun WordProficiency.asValue() = when (this) {
    WordProficiency.Unset -> Unset
    WordProficiency.Beginner -> Beginner
    WordProficiency.Intermediate -> Intermediate
    WordProficiency.Proficient -> Proficient
}

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

internal fun MeaningQuizStatsEntity.asExternalModel() = MeaningQuizStats.fromDb(
    wordId = wordId,
    easeFactor = easeFactor,
    intervalDays = intervalDays,
    nextQuizDate = nextQuizDate
)

internal fun TranslationQuizStatsEntity.asExternalModel() = TranslationQuizStats.fromDb(
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