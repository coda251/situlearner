package com.coda.situlearner.feature.home.explore.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import com.coda.situlearner.core.model.data.MediaType
import com.coda.situlearner.core.model.infra.SourceCollection
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.home.explore.library.util.AudioCoverFetcher
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ExploreLibraryScreen(
    onNavigateToCollection: (SourceCollection) -> Unit,
    onBack: () -> Unit,
    viewModel: ExploreLibraryViewModel = koinViewModel()
) {
    val videoUiState by viewModel.videoUiState.collectAsStateWithLifecycle()
    val audioUiState by viewModel.audioUiState.collectAsStateWithLifecycle()

    ExploreLibraryScreen(
        videoUiState = videoUiState,
        audioUiState = audioUiState,
        onClickCollection = onNavigateToCollection,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreLibraryScreen(
    videoUiState: ExploreLibraryUiState,
    audioUiState: ExploreLibraryUiState,
    onClickCollection: (SourceCollection) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val localImageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(AudioCoverFetcher.Factory())
            }
            .build()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_explore_library_screen_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = { BackButton(onBack) }
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ExploreSourceBoard(
                mediaType = MediaType.Video,
                uiState = videoUiState,
                imageLoader = localImageLoader,
                onClickCollection = onClickCollection
            )

            ExploreSourceBoard(
                mediaType = MediaType.Audio,
                uiState = audioUiState,
                imageLoader = localImageLoader,
                onClickCollection = onClickCollection
            )
        }
    }
}

@Composable
private fun ExploreSourceBoard(
    mediaType: MediaType,
    uiState: ExploreLibraryUiState,
    imageLoader: ImageLoader,
    onClickCollection: (SourceCollection) -> Unit
) {
    when (uiState) {
        is ExploreLibraryUiState.Loading -> {}
        is ExploreLibraryUiState.Empty -> {}
        is ExploreLibraryUiState.Success -> {
            Column {
                ListItem(
                    headlineContent = { Text(text = mediaType.asText()) },
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.collections,
                        key = { it.url }
                    ) {
                        SourceCollectionItem(
                            collection = it,
                            imageLoader = imageLoader,
                            onNavigateToCollection = onClickCollection,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceCollectionItem(
    collection: SourceCollection,
    imageLoader: ImageLoader,
    onNavigateToCollection: (SourceCollection) -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onNavigateToCollection(collection) }
    ) {
        AsyncMediaImage(
            model = collection,
            imageLoader = imageLoader,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(6.dp)),
        )

        Text(
            text = collection.name,
            modifier = Modifier.padding(vertical = 8.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )
    }
}

@Composable
private fun MediaType.asText() = when (this) {
    MediaType.Video -> stringResource(R.string.home_explore_library_screen_video)
    MediaType.Audio -> stringResource(R.string.home_explore_library_screen_audio)
}

@Preview
@Composable
private fun ExploreCollectionsScreenPreview() {
    val videoUiState = ExploreLibraryUiState.Success(
        collections = listOf(
            SourceCollection(
                name = "test_0",
                url = "0",
                idInDb = "0"
            ),
            SourceCollection(
                name = "test_1",
                url = "1",
                idInDb = null
            ),
            SourceCollection(
                name = "test_2",
                url = "2",
                idInDb = "2"
            )
        )
    )

    val audioUiState = ExploreLibraryUiState.Success(
        collections = listOf(
            SourceCollection(
                name = "test_0",
                url = "0",
                idInDb = "4"
            ),
            SourceCollection(
                name = "test_1",
                url = "1",
                idInDb = null
            ),
            SourceCollection(
                name = "test_2",
                url = "2",
                idInDb = null
            )
        )
    )

    ExploreLibraryScreen(
        videoUiState = videoUiState,
        audioUiState = audioUiState,
        onClickCollection = {},
        onBack = {}
    )
}