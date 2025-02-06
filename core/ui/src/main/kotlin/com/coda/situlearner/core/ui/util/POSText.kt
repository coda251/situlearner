package com.coda.situlearner.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.core.model.data.PartOfSpeech
import com.coda.situlearner.core.ui.R

@Composable
fun PartOfSpeech.asText(): String = when (this) {
    PartOfSpeech.Unknown -> stringResource(R.string.core_ui_part_of_speech_unknown)
    PartOfSpeech.Noun -> stringResource(R.string.core_ui_part_of_speech_noun)
    PartOfSpeech.Verb -> stringResource(R.string.core_ui_part_of_speech_verb)
    PartOfSpeech.Adjective -> stringResource(R.string.core_ui_part_of_speech_adjective)
    PartOfSpeech.Adverb -> stringResource(R.string.core_ui_part_of_speech_adverb)
}