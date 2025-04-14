package com.coda.situlearner.core.ui.widget

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource

@Composable
fun BackButton(
    onBack: () -> Unit,
    rotated: Boolean = false,
) {
    IconButton(
        onClick = onBack,
    ) {
        Icon(
            painter = painterResource(com.coda.situlearner.core.ui.R.drawable.arrow_back_24dp_000000_fill0_wght400_grad0_opsz24),
            contentDescription = null,
            modifier = if (rotated) Modifier.rotate(180f) else Modifier
        )
    }
}