package com.coda.situlearner.core.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferenceDataSource {

    val userPreferenceProto: Flow<UserPreferenceProto>

    suspend fun setWordLibraryLanguageProto(languageProto: LanguageProto)

    suspend fun setDarkThemeModeProto(darkThemeModeProto: DarkThemeModeProto)

    suspend fun setThemeColorModeProto(themeColorModeProto: ThemeColorModeProto)

    suspend fun setQuizWordCountProto(quizWordCount: UInt)

    suspend fun setRecommendedWordCountProto(recommendedWordCount: UInt)

    suspend fun setThumbnailThemeColorProto(color: Long)
}