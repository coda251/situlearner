package com.coda.situlearner.feature.player.fullscreen.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.coda.situlearner.feature.player.fullscreen.R

@Composable
internal fun AccelerationChip(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AssistChip(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
            onClick = { },
            label = { Text(text = "2 ×") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.keyboard_double_arrow_right_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color.Gray.copy(0.5f),
                labelColor = Color.White,
                leadingIconContentColor = Color.White
            ),
            border = AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor = Color.Transparent
            )
        )
    }
}