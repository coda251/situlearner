package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.ThemeColorModeProto
import com.coda.situlearner.core.model.data.ThemeColorMode

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