package com.coda.situlearner.feature.home.explore.collection

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.cfg.AppConfig
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.SourceCollectionWithFiles
import com.coda.situlearner.core.ui.widget.LanguageSelector
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WorkerDialog(
    collectionWithFiles: SourceCollectionWithFiles,
    onDismiss: () -> Unit,
    viewModel: WorkerDialogViewModel = koinViewModel()
) {
    val workerUiState by viewModel.workerUiState.collectAsStateWithLifecycle()

    WorkerDialog(
        workerStatusUiState = workerUiState,
        languageChoices = AppConfig.sourceLanguages,
        onSelectLanguage = { viewModel.insert(collectionWithFiles, it) },
        onDismiss = onDismiss,
    )
}

@Composable
private fun WorkerDialog(
    workerStatusUiState: WorkerStatusUiState,
    languageChoices: List<Language>,
    onSelectLanguage: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},
        text = {
            when (workerStatusUiState) {
                WorkerStatusUiState.Idle -> {
                    LanguageSelector(
                        choices = languageChoices,
                        currentLanguage = null,
                        onSelect = onSelectLanguage
                    )
                }

                WorkerStatusUiState.Ongoing -> {
                    WorkerStatusBoard()
                }

                WorkerStatusUiState.Done -> {
                    LaunchedEffect(Unit) {
                        onDismiss()
                    }
                }
            }
        }
    )
}

@Composable
private fun WorkerStatusBoard() {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(R.string.home_explore_collection_screen_adding_to_db)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Preview
@Composable
private fun WorkerDialogPreview() {
    var workerStatusUiState by remember {
        mutableStateOf<WorkerStatusUiState>(WorkerStatusUiState.Idle)
    }

    WorkerDialog(
        workerStatusUiState = workerStatusUiState,
        onSelectLanguage = { workerStatusUiState = WorkerStatusUiState.Ongoing },
        languageChoices = AppConfig.sourceLanguages,
        onDismiss = {}
    )
}