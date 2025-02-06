package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.UserPreferenceProto
import com.coda.situlearner.core.model.data.UserPreference

internal fun UserPreferenceProto.asExternalModel() = UserPreference(
    wordFilterLanguage = wordFilterLanguage.asExternalModel(),
    wordCategoryType = wordCategoryType.asExternalModel(),
    darkThemeMode = darkThemeMode.asExternalModel(),
    themeColorMode = themeColorMode.asExternalModel()
)