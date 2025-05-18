package com.coda.situlearner.feature.home.explore.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.infra.SourceFile
import com.coda.situlearner.core.ui.widget.BackButton
import org.koin.androidx.compose.koinViewModel
import kotlin.math.log10
import kotlin.math.pow

@Composable
internal fun ExploreCollectionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreCollectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreCollectionScreen(
        uiState = uiState,
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreCollectionScreen(
    uiState: ExploreCollectionUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    when (uiState) {
                        is ExploreCollectionUiState.Success -> {
                            AddCollectionAction(uiState = uiState)
                        }

                        else -> {}
                    }
                },
                navigationIcon = { BackButton(onBack = onBack) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                ExploreCollectionUiState.Loading -> {}
                ExploreCollectionUiState.Error -> {}
                is ExploreCollectionUiState.Empty -> {}
                is ExploreCollectionUiState.Success -> {
                    ExploreCollectionContentBoard(
                        files = uiState.collectionWithFiles.files
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCollectionAction(uiState: ExploreCollectionUiState.Success) {
    var showWorkerDialog by remember {
        mutableStateOf(false)
    }
    val showAddButton = uiState.collectionWithFiles.collection.idInDb == null

    if (showAddButton) {
        TextButton(
            onClick = { showWorkerDialog = true }
        ) {
            Text(
                text = stringResource(R.string.home_explore_collection_screen_add)
            )
        }
    }

    if (showWorkerDialog) {
        WorkerDialog(
            collectionWithFiles = uiState.collectionWithFiles,
            onDismiss = { showWorkerDialog = false }
        )
    }
}

@Composable
private fun ExploreCollectionContentBoard(
    files: List<SourceFile>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 88.dp),
        modifier = modifier,
    ) {
        itemsIndexed(
            items = files,
            key = { _, it -> it.hashCode() }
        ) { index, it ->
            SourceFileItemView(
                exploreFile = it,
                index = index,
            )
        }
    }
}

@Composable
private fun SourceFileItemView(
    exploreFile: SourceFile,
    index: Int
) {
    val leadingContent: @Composable (() -> Unit) = index.let {
        {
            Text(
                text = (it + 1).toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(24.dp)
            )
        }
    }

    val headlineContent: @Composable (() -> Unit) = {
        Text(text = exploreFile.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
    }

    val supportingContent: @Composable (() -> Unit) = {
        Text(text = exploreFile.metaDataString())
    }

    ListItem(
        headlineContent = headlineContent,
        leadingContent = leadingContent,
        supportingContent = supportingContent,
    )
}

@Composable
private fun SourceFile.metaDataString(): String = listOfNotNull(
    subtitleUrl?.let { stringResource(id = R.string.home_explore_collection_screen_subtitles) }
        ?: stringResource(id = R.string.home_explore_collection_screen_no_subtitles),
    mediaName.substringAfterLast('.')
        .ifEmpty { stringResource(id = R.string.home_explore_collection_screen_unknown_file_format) },
    mediaSize?.toReadableSize(),
).joinToString(separator = " • ")

private fun Long.toReadableSize(): String {
    if (this < 0) throw IllegalArgumentException()

    val unit = 1000 // storage format rather than ram format
    if (this < unit) return "$this B"

    val exp = (log10(this.toDouble()) / log10(unit.toDouble())).toInt()
    val pre = ("KMGTPE")[exp - 1] + "B"
    val size = this / unit.toDouble().pow(exp.toDouble())
    return "%.1f %s".format(size, pre)
}