package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.RepeatModeProto
import com.coda.situlearner.core.model.data.RepeatMode

internal fun RepeatMode.asProto() = when (this) {
    RepeatMode.One -> RepeatModeProto.REPEAT_MODE_ONE
    RepeatMode.All -> RepeatModeProto.REPEAT_MODE_ALL
}

internal fun RepeatModeProto.asExternalModel() = when (this) {
    RepeatModeProto.REPEAT_MODE_ONE, RepeatModeProto.UNRECOGNIZED -> RepeatMode.One
    RepeatModeProto.REPEAT_MODE_ALL -> RepeatMode.All
}