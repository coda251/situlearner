package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore

internal class LocalUserPreferenceDataSource(private val userPreference: DataStore<UserPreferenceProto>) :
    UserPreferenceDataSource {

    override val userPreferenceProto = userPreference.data

    override suspend fun setWordFilterLanguageProto(languageProto: LanguageProto) {
        userPreference.updateData {
            it.copy {
                this.wordFilterLanguage = languageProto
            }
        }
    }

    override suspend fun setWordCategoryTypeProto(wordCategoryTypeProto: WordCategoryTypeProto) {
        userPreference.updateData {
            it.copy {
                this.wordCategoryType = wordCategoryTypeProto
            }
        }
    }

    override suspend fun setDarkThemeModeProto(darkThemeModeProto: DarkThemeModeProto) {
        userPreference.updateData {
            it.copy {
                this.darkThemeMode = darkThemeModeProto
            }
        }
    }

    override suspend fun setThemeColorModeProto(themeColorModeProto: ThemeColorModeProto) {
        userPreference.updateData {
            it.copy {
                this.themeColorMode = themeColorModeProto
            }
        }
    }
}