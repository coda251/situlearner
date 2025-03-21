package com.coda.situlearner.core.model.data.mapper

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.UserPreference

fun UserPreference.resolveLanguage(defaultSourceLanguage: Language): UserPreference {
    if (wordLibraryLanguage != Language.Unknown) return this
    else {
        check(defaultSourceLanguage != Language.Unknown)
        return this.copy(wordLibraryLanguage = defaultSourceLanguage)
    }
}