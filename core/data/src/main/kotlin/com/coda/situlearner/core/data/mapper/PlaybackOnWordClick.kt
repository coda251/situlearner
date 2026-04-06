package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.PlaybackOnWordClickProto
import com.coda.situlearner.core.model.data.PlaybackOnWordClick

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