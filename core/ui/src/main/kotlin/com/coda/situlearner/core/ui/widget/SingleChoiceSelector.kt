package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.coda.situlearner.core.ui.R

@Composable
fun <T> SingleChoiceSelector(
    currentValue: T,
    choices: List<T>,
    headline: String,
    supportingText: String,
    valueToText: @Composable (T) -> String,
    onConfirm: (T) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = headline) },
        supportingContent = { Text(text = supportingText) },
        modifier = Modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.core_ui_ok))
                }
            },
            text = {
                Column {
                    choices.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = it == currentValue,
                                onClick = { onConfirm(it) },
                            )
                            Text(text = valueToText(it))
                        }
                    }
                }
            }
        )
    }
}