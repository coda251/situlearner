package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asProto
import com.coda.situlearner.core.datastore.UserPreferenceDataSource
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.model.data.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalUserPreferenceRepository(
    private val dataSource: UserPreferenceDataSource,
    defaultSourceLanguage: Language = AppConfig.defaultSourceLanguage,
    defaultQuizWordCount: UInt = AppConfig.DEFAULT_QUIZ_WORD_COUNT,
    defaultRecommendedWordCount: UInt = AppConfig.DEFAULT_RECOMMENDED_WORD_COUNT
) : UserPreferenceRepository {

    override val userPreference: Flow<UserPreference> =
        dataSource.userPreferenceProto.map {
            it.asExternalModel(
                defaultSourceLanguage,
                defaultQuizWordCount,
                defaultRecommendedWordCount
            )
        }

    override suspend fun setWordLibraryLanguage(language: Language) {
        dataSource.setWordLibraryLanguageProto(language.asProto())
    }

    override suspend fun setDarkThemeMode(darkThemeMode: DarkThemeMode) {
        dataSource.setDarkThemeModeProto(darkThemeMode.asProto())
    }

    override suspend fun setThemeColorMode(themeColorMode: ThemeColorMode) {
        dataSource.setThemeColorModeProto(themeColorMode.asProto())
    }

    override suspend fun setQuizWordCount(quizWordCount: UInt) {
        dataSource.setQuizWordCountProto(quizWordCount)
    }

    override suspend fun setRecommendedWordCount(recommendedWordCount: UInt) {
        dataSource.setRecommendedWordCountProto(recommendedWordCount)
    }
}