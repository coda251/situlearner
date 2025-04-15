package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.ui.R
import com.coda.situlearner.core.ui.util.asText

@Composable
fun LanguageSelectorDialog(
    choices: List<Language>,
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSelect: (Language) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(R.string.core_ui_ok))
            }
        },
        text = {
            LanguageSelector(
                choices = choices,
                currentLanguage = currentLanguage,
                onSelect = onSelect
            )
        }
    )
}

@Composable
fun LanguageSelector(
    choices: List<Language>,
    currentLanguage: Language?,
    onSelect: (Language) -> Unit
) {
    Column {
        choices.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = it == currentLanguage,
                    onClick = { onSelect(it) },
                )
                Text(text = it.asText())
            }
        }
    }
}