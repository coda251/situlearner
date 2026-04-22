package com.coda.situlearner.feature.home.media.edit

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.MediaCollection
import com.coda.situlearner.core.ui.widget.AsyncMediaImage
import com.coda.situlearner.core.ui.widget.BackButton
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun EditScreen(
    onBack: () -> Unit,
    viewModel: EditViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditScreen(
        uiState = uiState,
        onBack = onBack,
        onSave = viewModel::updateCollection
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditScreen(
    uiState: UiState,
    onBack: () -> Unit,
    onSave: (MediaCollection) -> Unit,
) {
    var currentCollection by remember(uiState) {
        mutableStateOf(
            if (uiState is UiState.Success) uiState.collection
            else null
        )
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Saved -> onBack()
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackButton(onBack)
                },
                actions = {
                    currentCollection?.let {
                        TextButton(
                            onClick = {
                                if (uiState is UiState.Success) {
                                    if (uiState.collection == it) onBack()
                                    else onSave(it)
                                }
                            }
                        ) {
                            Text(text = stringResource(coreR.string.core_ui_ok))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            currentCollection?.let { c ->
                ContentBoard(
                    collection = c,
                    onChange = {
                        currentCollection = it
                    }
                )
            }
        }
    }
}

@Composable
private fun ContentBoard(
    collection: MediaCollection,
    onChange: (MediaCollection) -> Unit,
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            // use property originalCoverImageUrl to record the uri of cover image
            onChange(
                collection.copy(
                    coverImageUrl = null,
                    originalCoverImageUrl = it.toString()
                )
            )
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncMediaImage(
                // coverImageUrl for cached image url and originalCoverImageUrl for uri
                model = collection.coverImageUrl ?: collection.originalCoverImageUrl,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .align(Alignment.Center)
                    .clickable {
                        launcher.launch(arrayOf("image/*"))
                    },
                contentScale = ContentScale.Crop
            )
        }

        OutlinedTextField(
            value = collection.name,
            onValueChange = { onChange(collection.copy(name = it)) },
            label = { Text(stringResource(R.string.home_media_edit_screen_name)) },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = collection.url,
            onValueChange = {},
            enabled = false,
            label = { Text(stringResource(R.string.home_media_edit_screen_directory)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
@Preview
private fun ScreenPreview() {
    EditScreen(
        uiState = UiState.Success(
            MediaCollection(
                id = "",
                name = "Test",
                url = "file:///storage/emulated/0/Download/fake",
            )
        ),
        onBack = {},
        onSave = {}
    )
}