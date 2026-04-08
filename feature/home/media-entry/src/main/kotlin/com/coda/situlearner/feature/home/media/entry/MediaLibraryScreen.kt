package com.coda.situlearner.feature.home.media.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.testing.data.mediaCollectionsTestData
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun MediaLibraryScreen(
    onNavigateToCollection: (MediaCollection) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MediaLibraryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaLibraryScreen(
        uiState = uiState,
        onClickCollection = onNavigateToCollection,
        onAdd = onNavigateToExplore,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaLibraryScreen(
    uiState: MediaLibraryUiState,
    onClickCollection: (MediaCollection) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.home_media_library_screen_title))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
            ) {
                Icon(
                    painter = painterResource(coreR.drawable.add_24dp_000000_fill0_wght400_grad0_opsz24),
                    contentDescription = null
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                MediaLibraryUiState.Empty -> {}

                MediaLibraryUiState.Loading -> {}

                is MediaLibraryUiState.Success -> {
                    MediaLibraryContentBoard(
                        collections = uiState.collections,
                        onClickCollection = onClickCollection,
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaLibraryContentBoard(
    collections: List<MediaCollection>,
    onClickCollection: (MediaCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = collections,
            key = { it.hashCode() }
        ) { collection ->
            MediaCollectionItem(
                mediaCollection = collection,
                onClickCollection = onClickCollection,
            )
        }
    }
}

@Composable
private fun MediaCollectionItem(
    mediaCollection: MediaCollection,
    onClickCollection: (MediaCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.clickable(
            onClick = { onClickCollection(mediaCollection) }
        ),
        leadingContent = {
            AsyncMediaImage(
                mediaCollection.coverImageUrl,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        },
        headlineContent = {
            Text(mediaCollection.name)
        }
    )
}

@Preview
@Composable
private fun MediaCollectionsScreenPreview() {
    val uiState = MediaLibraryUiState.Success(
        collections = mediaCollectionsTestData
    )

    MediaLibraryScreen(
        uiState = uiState,
        onClickCollection = {},
        onAdd = {}
    )
}