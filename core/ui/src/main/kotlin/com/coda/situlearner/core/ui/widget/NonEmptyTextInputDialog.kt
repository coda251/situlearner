package com.coda.situlearner.core.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.coda.situlearner.core.ui.R
import kotlinx.coroutines.delay

@Composable
fun NonEmptyTextInputDialog(
    text: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    var currentText by rememberSaveable(text, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        )
    }

    var isEmpty by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (currentText.text.isEmpty()) {
                        isEmpty = true
                        return@TextButton
                    }

                    onConfirm(currentText.text)
                }
            ) {
                Text(text = stringResource(R.string.core_ui_confirm))
            }
        },
        text = {
            OutlinedTextField(
                value = currentText,
                onValueChange = {
                    currentText = it
                    if (isEmpty) isEmpty = false
                },
                isError = isEmpty,
                supportingText = {
                    AnimatedVisibility(visible = isEmpty) {
                        Text(text = stringResource(R.string.core_ui_empty_error))
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(visible = isEmpty) {
                        Icon(
                            painter = painterResource(R.drawable.error_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.focusRequester(focusRequester)
            )
        }
    )

    LaunchedEffect(key1 = Unit) {
        delay(200)
        focusRequester.requestFocus()
    }
}