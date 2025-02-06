package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.ui.R

@Composable
fun LanguageChips(
    selectedLanguage: Language?,
    languageChoices: List<Language>,
    onSelectLanguage: (Language) -> Unit
) {
    ListItem(
        leadingContent = {
            Text(
                text = stringResource(R.string.core_ui_language),
            )
        },
        headlineContent = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = languageChoices,
                    key = { it.name }
                ) {
                    FilterChip(
                        selected = it == selectedLanguage,
                        onClick = { onSelectLanguage(it) },
                        label = { Text(text = it.asText()) }
                    )
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun Language.asText(): String = when (this) {
    Language.Unknown -> stringResource(R.string.core_ui_language_unknown)
    Language.Chinese -> stringResource(R.string.core_ui_language_chinese)
    Language.English -> stringResource(R.string.core_ui_language_english)
    Language.Japanese -> stringResource(R.string.core_ui_language_japanese)
}