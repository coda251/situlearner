package com.coda.situlearner.core.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.imageLoader
import com.coda.situlearner.core.ui.R

@Composable
fun AsyncMediaImage(
    model: Any?,
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader = LocalContext.current.imageLoader,
) {
    SubcomposeAsyncImage(
        model = model,
        imageLoader = imageLoader,
        contentDescription = null,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize(0.5f))
            }
        },
        error = {
            MediaImageErrorPlaceHolder(
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}

@Composable
private fun MediaImageErrorPlaceHolder(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.outlineVariant,
    contentColor: Color = MaterialTheme.colorScheme.outline,
    // the image should be placed in a square shape since
    // the placeholder drawable is fixed in square shape
    placeholder: Painter = painterResource(R.drawable.stock_media_24dp_000000_fill0_wght400_grad0_opsz24)
) {
    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = placeholder,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.75f),
            tint = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AsyncImagePreview() {
    AsyncMediaImage(
        null, modifier = Modifier
            .size(40.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
    )
}