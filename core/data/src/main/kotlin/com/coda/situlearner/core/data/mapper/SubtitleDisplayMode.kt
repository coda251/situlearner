package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.SubtitleDisplayModeProto
import com.coda.situlearner.core.model.data.SubtitleDisplayMode

internal fun SubtitleDisplayMode.asProto() = when (this) {
    SubtitleDisplayMode.All -> SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ALL
    SubtitleDisplayMode.OnlySourceText -> SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ONLY_SOURCE_TEXT
}

internal fun SubtitleDisplayModeProto.asExternalModel() = when (this) {
    SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ALL, SubtitleDisplayModeProto.UNRECOGNIZED -> SubtitleDisplayMode.All
    SubtitleDisplayModeProto.SUBTITLE_DISPLAY_MODE_ONLY_SOURCE_TEXT -> SubtitleDisplayMode.OnlySourceText
}