package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.model.data.UserPreference
import com.coda.situlearner.core.model.data.WordBookSortBy
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    val userPreference: Flow<UserPreference>

    suspend fun setWordLibraryLanguage(language: Language)

    suspend fun setDarkThemeMode(darkThemeMode: DarkThemeMode)

    suspend fun setThemeColorMode(themeColorMode: ThemeColorMode)

    suspend fun setQuizWordCount(quizWordCount: UInt)

    suspend fun setRecommendedWordCount(recommendedWordCount: UInt)

    suspend fun setThumbnailThemeColor(color: Long)

    suspend fun setWordBookSortBy(wordBookSortBy: WordBookSortBy)
}