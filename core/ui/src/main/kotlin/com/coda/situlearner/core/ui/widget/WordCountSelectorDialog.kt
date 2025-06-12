package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coda.situlearner.core.ui.R
import kotlin.math.roundToInt

@Composable
fun WordCountSelectorDialog(
    initialCount: UInt,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onDismiss: () -> Unit,
    onConfirm: (UInt) -> Unit,
) {
    var count by remember {
        mutableStateOf(initialCount)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(count) }
            ) {
                Text(text = stringResource(R.string.core_ui_ok))
            }
        },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.core_ui_count)) },
                    trailingContent = { Text("$count") },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                Slider(
                    modifier = Modifier.padding(12.dp),
                    value = count.toFloat(),
                    onValueChange = { count = it.roundToInt().toUInt() },
                    valueRange = valueRange,
                    steps = steps
                )
            }
        }
    )
}