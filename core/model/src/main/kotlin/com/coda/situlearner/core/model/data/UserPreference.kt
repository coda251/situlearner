package com.coda.situlearner.core.model.data

data class UserPreference(
    val wordLibraryLanguage: Language,
    val darkThemeMode: DarkThemeMode,
    val themeColorMode: ThemeColorMode,
    val thumbnailThemeColor: Long,
    val quizWordCount: UInt,
    val recommendedWordCount: UInt,
)