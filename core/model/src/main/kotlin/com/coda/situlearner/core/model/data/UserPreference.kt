package com.coda.situlearner.core.model.data

data class UserPreference(
    val wordLibraryLanguage: Language,
    val darkThemeMode: DarkThemeMode,
    val themeColorMode: ThemeColorMode,
    val quizWordCount: UInt,
)