package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.WordCategoryTypeProto
import com.coda.situlearner.core.model.data.WordCategoryType

internal fun WordCategoryType.asProto() = when (this) {
    WordCategoryType.LastViewedDate -> WordCategoryTypeProto.WORD_CATEGORY_TYPE_LAST_VIEWED_DATE
    WordCategoryType.Proficiency -> WordCategoryTypeProto.WORD_CATEGORY_TYPE_PROFICIENCY
    WordCategoryType.Media -> WordCategoryTypeProto.WORD_CATEGORY_TYPE_MEDIA
    WordCategoryType.PartOfSpeech -> WordCategoryTypeProto.WORD_CATEGORY_TYPE_PART_OF_SPEECH
}

internal fun WordCategoryTypeProto.asExternalModel() = when (this) {
    WordCategoryTypeProto.WORD_CATEGORY_TYPE_UNSPECIFIED, WordCategoryTypeProto.UNRECOGNIZED -> WordCategoryType.LastViewedDate // as default
    WordCategoryTypeProto.WORD_CATEGORY_TYPE_LAST_VIEWED_DATE -> WordCategoryType.LastViewedDate
    WordCategoryTypeProto.WORD_CATEGORY_TYPE_PROFICIENCY -> WordCategoryType.Proficiency
    WordCategoryTypeProto.WORD_CATEGORY_TYPE_MEDIA -> WordCategoryType.Media
    WordCategoryTypeProto.WORD_CATEGORY_TYPE_PART_OF_SPEECH -> WordCategoryType.PartOfSpeech
}