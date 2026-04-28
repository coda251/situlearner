package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.DarkThemeModeProto
import com.coda.situlearner.core.datastore.PlaybackOnWordClickProto
import com.coda.situlearner.core.datastore.QuizDueModeProto
import com.coda.situlearner.core.datastore.SubtitleDisplayModeProto
import com.coda.situlearner.core.datastore.ThemeColorModeProto
import com.coda.situlearner.core.datastore.UserPreferenceProto
import com.coda.situlearner.core.datastore.WordBookSortByProto
import com.coda.situlearner.core.model.data.DarkThemeMode
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.PlaybackOnWordClick
import com.coda.situlearner.core.model.data.QuizDueMode
import com.coda.situlearner.core.model.data.SubtitleDisplayMode
import com.coda.situlearner.core.model.data.ThemeColorMode
import com.coda.situlearner.core.model.data.UserPreference
import com.coda.situlearner.core.model.data.WordBookSortBy

internal fun PlaybackOnWordClick.asProto() = when (this) {
    PlaybackOnWordClick.Unchange -> PlaybackOnWordClickProto.PLAYBACK_ON_WORD_CLICK_UNCHANGE
    PlaybackOnWordClick.Pause -> PlaybackOnWordClickProto.PLAYBACK_ON_WORD_CLICK_PAUSE
    PlaybackOnWordClick.PlayInLoop -> PlaybackOnWordClickProto.PLAYBACK_ON_WORD_CLICK_PLAY_IN_LOOP
}

internal fun PlaybackOnWordClickProto.asExternalModel() = when (this) {
    PlaybackOnWordClickProto.UNRECOGNIZED, PlaybackOnWordClickProto.PLAYBACK_ON_WORD_CLICK_UNCHANGE
        -> PlaybackOnWordClick.Unchange

    PlaybackOnWordClickProto.PLAYBACK_ON_WORD_CLICK_PAUSE -> PlaybackOnWordClick.Pause
    PlaybackOnWordClickProto.PLAYBACK_ON_WORD_CLICK_PLAY_IN_LOOP -> PlaybackOnWordClick.PlayInLoop
}

internal fun SubtitleDisplayMode.asProto() = when (this) {
    SubtitleDisplayMode.All -> SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ALL
    SubtitleDisplayMode.OnlySourceText -> SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ONLY_SOURCE_TEXT
}

internal fun SubtitleDisplayModeProto.asExternalModel() = when (this) {
    SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ALL, SubtitleDisplayModeProto.UNRECOGNIZED -> SubtitleDisplayMode.All
    SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ONLY_SOURCE_TEXT -> SubtitleDisplayMode.OnlySourceText
}

internal fun QuizDueMode.asProto() = when (this) {
    QuizDueMode.Now -> QuizDueModeProto.QUIZ_DUE_MODE_NOW
    QuizDueMode.Today -> QuizDueModeProto.QUIZ_DUE_MODE_TODAY
}

internal fun QuizDueModeProto.asExternalModel() = when (this) {
    QuizDueModeProto.QUIZ_DUE_MODE_NOW, QuizDueModeProto.UNRECOGNIZED -> QuizDueMode.Now
    QuizDueModeProto.QUIZ_DUE_MODE_TODAY -> QuizDueMode.Today
}

internal fun WordBookSortBy.asProto(): WordBookSortByProto = when (this) {
    WordBookSortBy.Count -> WordBookSortByProto.WORD_BOOK_SORT_BY_COUNT
    WordBookSortBy.UpdatedDate -> WordBookSortByProto.WORD_BOOK_SORT_BY_UPDATED_DATE
}

internal fun WordBookSortByProto.asExternalModel(): WordBookSortBy = when (this) {
    WordBookSortByProto.UNRECOGNIZED, WordBookSortByProto.WORD_BOOK_SORT_BY_COUNT ->
        WordBookSortBy.Count

    WordBookSortByProto.WORD_BOOK_SORT_BY_UPDATED_DATE -> WordBookSortBy.UpdatedDate
}

internal fun ThemeColorMode.asProto() = when (this) {
    ThemeColorMode.Static -> ThemeColorModeProto.THEME_COLOR_MODE_STATIC
    ThemeColorMode.DynamicWithThumbnail -> ThemeColorModeProto.THEME_COLOR_MODE_DYNAMIC_WITH_THUMBNAIL
    ThemeColorMode.DynamicWithWallpaper -> ThemeColorModeProto.THEME_COLOR_MODE_DYNAMIC_WITH_WALLPAPER
}

internal fun ThemeColorModeProto.asExternalModel() = when (this) {
    ThemeColorModeProto.UNRECOGNIZED, ThemeColorModeProto.THEME_COLOR_MODE_STATIC ->
        ThemeColorMode.Static

    ThemeColorModeProto.THEME_COLOR_MODE_DYNAMIC_WITH_THUMBNAIL -> ThemeColorMode.DynamicWithThumbnail
    ThemeColorModeProto.THEME_COLOR_MODE_DYNAMIC_WITH_WALLPAPER -> ThemeColorMode.DynamicWithWallpaper
}

internal fun DarkThemeMode.asProto() = when (this) {
    DarkThemeMode.Light -> DarkThemeModeProto.DARK_THEME_MODE_LIGHT
    DarkThemeMode.Dark -> DarkThemeModeProto.DARK_THEME_MODE_DARK
    DarkThemeMode.FollowSystem -> DarkThemeModeProto.DARK_THEME_MODE_FOLLOW_SYSTEM
}

internal fun DarkThemeModeProto.asExternalModel() = when (this) {
    DarkThemeModeProto.UNRECOGNIZED, DarkThemeModeProto.DARK_THEME_MODE_FOLLOW_SYSTEM ->
        DarkThemeMode.FollowSystem

    DarkThemeModeProto.DARK_THEME_MODE_LIGHT -> DarkThemeMode.Light
    DarkThemeModeProto.DARK_THEME_MODE_DARK -> DarkThemeMode.Dark
}

internal fun UserPreferenceProto.asExternalModel(
    defaultWordLibraryLanguage: Language,
    defaultQuizWordCount: UInt,
    defaultRecommendedWordCount: UInt,
    defaultThemeColor: Long
) = UserPreference(
    wordLibraryLanguage = wordLibraryLanguage.asExternalModel().takeIf { it != Language.Unknown }
        ?: defaultWordLibraryLanguage,
    darkThemeMode = darkThemeMode.asExternalModel(),
    themeColorMode = themeColorMode.asExternalModel(),
    thumbnailThemeColor = thumbnailThemeColor.takeIf { it != 0L } ?: defaultThemeColor,
    quizWordCount = quizWordCount.toUInt().takeIf { it != 0u } ?: defaultQuizWordCount,
    recommendedWordCount = recommendedWordCount.toUInt().takeIf { it != 0u }
        ?: defaultRecommendedWordCount,
    wordBookSortBy = wordBookSortBy.asExternalModel(),
    playbackOnWordClick = playbackOnWordClick.asExternalModel(),
    subtitleDisplayMode = subtitleDisplayMode.asExternalModel(),
    quizDueMode = quizDueMode.asExternalModel()
)