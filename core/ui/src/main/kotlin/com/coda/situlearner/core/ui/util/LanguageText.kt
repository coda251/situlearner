package com.coda.situlearner.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.ui.R

@Composable
fun Language.asText(): String = when (this) {
    Language.Unknown -> stringResource(R.string.core_ui_language_unknown)
    Language.Chinese -> stringResource(R.string.core_ui_language_chinese)
    Language.English -> stringResource(R.string.core_ui_language_english)
    Language.Japanese -> stringResource(R.string.core_ui_language_japanese)
}