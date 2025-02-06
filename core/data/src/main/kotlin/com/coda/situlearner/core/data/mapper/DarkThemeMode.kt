package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.DarkThemeModeProto
import com.coda.situlearner.core.model.data.DarkThemeMode

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