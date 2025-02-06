package com.coda.situlearner.core.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferenceDataSource {

    val userPreferenceProto: Flow<UserPreferenceProto>

    suspend fun setWordFilterLanguageProto(languageProto: LanguageProto)

    suspend fun setWordCategoryTypeProto(wordCategoryTypeProto: WordCategoryTypeProto)

    suspend fun setDarkThemeModeProto(darkThemeModeProto: DarkThemeModeProto)

    suspend fun setThemeColorModeProto(themeColorModeProto: ThemeColorModeProto)
}