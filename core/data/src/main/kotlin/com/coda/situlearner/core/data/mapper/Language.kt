package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.database.model.Chinese
import com.coda.situlearner.core.database.model.English
import com.coda.situlearner.core.database.model.Japanese
import com.coda.situlearner.core.database.model.Unknown_Language
import com.coda.situlearner.core.datastore.LanguageProto
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.database.model.Language as LanguageValue

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