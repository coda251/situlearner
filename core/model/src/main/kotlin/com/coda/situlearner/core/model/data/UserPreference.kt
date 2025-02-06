package com.coda.situlearner.core.model.data

data class UserPreference(
    val wordFilterLanguage: Language,
    val wordCategoryType: WordCategoryType,
    val darkThemeMode: DarkThemeMode,
    val themeColorMode: ThemeColorMode,
)