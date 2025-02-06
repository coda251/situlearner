package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.model.data.UserPreference
import com.coda.situlearner.core.model.data.WordCategoryType
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    val userPreference: Flow<UserPreference>

    suspend fun setWordFilterLanguage(language: Language)

    suspend fun setWordCategoryType(wordCategoryType: WordCategoryType)

    suspend fun setDarkThemeMode(darkThemeMode: DarkThemeMode)

    suspend fun setThemeColorMode(themeColorMode: ThemeColorMode)
}