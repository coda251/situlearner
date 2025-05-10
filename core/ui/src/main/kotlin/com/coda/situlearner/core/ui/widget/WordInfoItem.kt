package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.core.ui.R

/**
 * @param wordInfo the word info to display. Should not be empty (see [WordInfo.isNotEmpty]).
 * @param onBack navigate back to the word infos list (if needed).
 */
@Composable
fun WordInfoDetailItem(
    wordInfo: WordInfo,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        if (onBack != null) {
            ListItem(
                headlineContent = { Text(text = wordInfo.pronunciation ?: "") },
                trailingContent = { BackButton(onBack = onBack, rotated = true) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        } else {
            wordInfo.pronunciation?.let {
                ListItem(
                    headlineContent = { Text(text = it) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }

        LazyColumn {
            items(
                items = wordInfo.meanings,
                key = { it.partOfSpeechTag }
            ) {
                ListItem(
                    headlineContent = { Text(text = it.definition) },
                    overlineContent = { Text(text = it.partOfSpeechTag) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun WordInfoEmptyItem(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.core_ui_no_pronunciation_and_meanings)
    )
}