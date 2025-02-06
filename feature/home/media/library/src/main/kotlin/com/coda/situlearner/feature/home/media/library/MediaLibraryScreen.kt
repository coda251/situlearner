package com.coda.situlearner.feature.home.media.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.testing.data.mediaCollectionsTestData
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun MediaLibraryScreen(
    onNavigateToCollection: (MediaCollection) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MediaLibraryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaLibraryScreen(
        uiState = uiState,
        onClickCollection = onNavigateToCollection,
        onDeleteCollection = viewModel::deleteMediaCollection,
        onRenameCollection = viewModel::setMediaCollectionName,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaLibraryScreen(
    uiState: MediaLibraryUiState,
    onClickCollection: (MediaCollection) -> Unit,
    onDeleteCollection: (MediaCollection) -> Unit,
    onRenameCollection: (MediaCollection, String) -> Unit,
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
                        onDeleteCollection = onDeleteCollection,
                        onRenameCollection = onRenameCollection,
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
    onDeleteCollection: (MediaCollection) -> Unit,
    onRenameCollection: (MediaCollection, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBottomSheetMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var currentCollection by remember { mutableStateOf<MediaCollection?>(null) }

    LazyColumn(modifier = modifier) {
        items(
            items = collections,
            key = { it.hashCode() }
        ) { collection ->
            MediaCollectionItem(
                mediaCollection = collection,
                onClickCollection = onClickCollection,
                onLongClickCollection = {
                    showBottomSheetMenu = true
                    currentCollection = it
                },
            )
        }
    }

    if (showBottomSheetMenu) {
        currentCollection?.let {
            CollectionMenuBottomSheet(
                title = it.name,
                onDismiss = {
                    showBottomSheetMenu = false
                    currentCollection = null
                },
                onDelete = {
                    showDeleteDialog = true
                    showBottomSheetMenu = false
                },
                onRename = {
                    showRenameDialog = true
                    showBottomSheetMenu = false
                }
            )
        }
    }

    if (showDeleteDialog) {
        currentCollection?.let {
            DeleteCollectionDialog(
                onDismiss = {
                    showDeleteDialog = false
                    currentCollection = null
                },
                onConfirm = {
                    onDeleteCollection(it)
                    showDeleteDialog = false
                    currentCollection = null
                }
            )
        }
    }

    if (showRenameDialog) {
        currentCollection?.let { collection ->
            RenameCollectionDialog(
                name = collection.name,
                onDismiss = {
                    showRenameDialog = false
                    currentCollection = null
                },
                onRename = {
                    onRenameCollection(collection, it)
                    showRenameDialog = false
                    currentCollection = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionMenuBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onRename: (() -> Unit)? = null
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = {}
    ) {
        ListItem(headlineContent = { Text(text = title) })

        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
        )

        onRename?.let {
            ListItem(
                modifier = Modifier.clickable(onClick = it),
                headlineContent = { Text(text = stringResource(id = R.string.home_media_library_screen_rename_collection)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.edit_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            )
        }

        onDelete?.let {
            ListItem(
                modifier = Modifier.clickable(onClick = it),
                headlineContent = { Text(text = stringResource(id = R.string.home_media_library_screen_delete_collection)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(coreR.drawable.delete_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun DeleteCollectionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(coreR.string.core_ui_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(coreR.string.core_ui_cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.home_media_library_screen_delete_collection_title))
        },
        text = {
            Text(text = stringResource(R.string.home_media_library_screen_delete_collection_text))
        },
        modifier = modifier
    )
}

@Composable
private fun RenameCollectionDialog(
    name: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    var collectionName by rememberSaveable(name, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = name,
                selection = TextRange(name.length)
            )
        )
    }

    var hasEmptyName by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    // validate
                    if (collectionName.text.isEmpty()) {
                        hasEmptyName = true
                        return@TextButton
                    }
                    onRename(collectionName.text)
                }
            ) {
                Text(text = stringResource(coreR.string.core_ui_confirm))
            }
        },
        text = {
            OutlinedTextField(
                value = collectionName,
                onValueChange = {
                    collectionName = it
                    if (hasEmptyName) hasEmptyName = false
                },
                label = { Text(text = stringResource(id = R.string.home_media_library_screen_rename_collection_label)) },
                isError = hasEmptyName,
                supportingText = {
                    AnimatedVisibility(visible = hasEmptyName) {
                        Text(
                            text = stringResource(
                                id = R.string.home_media_library_screen_rename_collection_empty_error
                            )
                        )
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(visible = hasEmptyName) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaCollectionItem(
    mediaCollection: MediaCollection,
    onClickCollection: (MediaCollection) -> Unit,
    onLongClickCollection: (MediaCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.combinedClickable(
            onClick = { onClickCollection(mediaCollection) },
            onLongClick = { onLongClickCollection(mediaCollection) }
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
        onRenameCollection = { _, _ -> },
        onDeleteCollection = {}
    )
}