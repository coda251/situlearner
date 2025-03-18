package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.cfg.LanguageConfig
import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asProto
import com.coda.situlearner.core.datastore.UserPreferenceDataSource
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.model.data.UserPreference
import com.coda.situlearner.core.model.data.mapper.resolveLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalUserPreferenceRepository(
    private val dataSource: UserPreferenceDataSource,
    defaultSourceLanguage: Language = LanguageConfig.sourceLanguages.first()
) : UserPreferenceRepository {

    override val userPreference: Flow<UserPreference> =
        dataSource.userPreferenceProto.map {
            it.asExternalModel().resolveLanguage(defaultSourceLanguage)
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
}