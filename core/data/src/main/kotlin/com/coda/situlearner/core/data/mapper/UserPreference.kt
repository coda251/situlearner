package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.UserPreferenceProto
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.UserPreference

internal fun UserPreferenceProto.asExternalModel(
    defaultWordLibraryLanguage: Language,
    defaultQuizWordCount: UInt,
    defaultRecommendedWordCount: UInt,
    defaultThemeColor: Long
) = UserPreference(
    wordLibraryLanguage = wordLibraryLanguage.asExternalModel().takeIf { it != Language.Unknown }
        ?: defaultWordLibraryLanguage,
    darkThemeMode = darkThemeMode.asExternalModel(),
    themeColorMode = themeColorMode.asExternalModel(),
    thumbnailThemeColor = thumbnailThemeColor.takeIf { it != 0L } ?: defaultThemeColor,
    quizWordCount = quizWordCount.toUInt().takeIf { it != 0u } ?: defaultQuizWordCount,
    recommendedWordCount = recommendedWordCount.toUInt().takeIf { it != 0u }
        ?: defaultRecommendedWordCount,
    wordBookSortBy = wordBookSortBy.asExternalModel()
)