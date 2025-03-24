package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore

internal class LocalUserPreferenceDataSource(private val userPreference: DataStore<UserPreferenceProto>) :
    UserPreferenceDataSource {

    override val userPreferenceProto = userPreference.data

    override suspend fun setWordLibraryLanguageProto(languageProto: LanguageProto) {
        userPreference.updateData {
            it.copy {
                this.wordLibraryLanguage = languageProto
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

    override suspend fun setQuizWordCountProto(quizWordCount: UInt) {
        userPreference.updateData {
            it.copy {
                this.quizWordCount = quizWordCount.toInt()
            }
        }
    }
}