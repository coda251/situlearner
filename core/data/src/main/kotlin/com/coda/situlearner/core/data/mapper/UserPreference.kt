package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.UserPreferenceProto
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.UserPreference

internal fun UserPreferenceProto.asExternalModel(
    defaultWordLibraryLanguage: Language,
    defaultQuizWordCount: UInt
) = UserPreference(
    wordLibraryLanguage = wordLibraryLanguage.asExternalModel().takeIf { it != Language.Unknown }
        ?: defaultWordLibraryLanguage,
    darkThemeMode = darkThemeMode.asExternalModel(),
    themeColorMode = themeColorMode.asExternalModel(),
    quizWordCount = quizWordCount.toUInt().takeIf { it != 0u } ?: defaultQuizWordCount,
)