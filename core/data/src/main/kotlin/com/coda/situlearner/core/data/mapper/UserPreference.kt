package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.UserPreferenceProto
import com.coda.situlearner.core.model.data.UserPreference

internal fun UserPreferenceProto.asExternalModel() = UserPreference(
    wordLibraryLanguage = wordLibraryLanguage.asExternalModel(),
    darkThemeMode = darkThemeMode.asExternalModel(),
    themeColorMode = themeColorMode.asExternalModel()
)