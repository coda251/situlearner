package com.coda.situlearner.feature.word.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.data.mapper.asWordInfo
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.core.testing.data.wordsTestData
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.core.ui.widget.NonEmptyTextInputDialog
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun WordEditScreen(
    onBack: () -> Unit,
    viewModel: WordEditViewModel = koinViewModel()
) {
    val editUiState by viewModel.editUiState.collectAsStateWithLifecycle()
    val queryUiState by viewModel.queryUiState.collectAsStateWithLifecycle()

    WordEditScreen(
        editUiState = editUiState,
        queryUiState = queryUiState,
        onBack = onBack,
        onSave = viewModel::updateWord,
        onQueryWord = viewModel::getTranslation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordEditScreen(
    editUiState: WordEditUiState,
    queryUiState: WordQueryUiState,
    onBack: () -> Unit,
    onSave: (Word) -> Unit,
    onQueryWord: (String, Language) -> Unit,
) {
    var currentWordInfo by remember(editUiState) {
        mutableStateOf(
            if (editUiState is WordEditUiState.Success) editUiState.word.asWordInfo()
            else null
        )
    }

    var showWordTranslationBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(editUiState) {
        when (editUiState) {
            is WordEditUiState.Saved -> onBack()
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    currentWordInfo?.let { info ->
                        WordPanel(
                            word = info.word,
                            onChange = {
                                currentWordInfo = info.copyFromUserInput(
                                    word = it
                                )
                            }
                        )
                    }
                },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    currentWordInfo?.let {
                        TextButton(
                            onClick = {
                                if (editUiState is WordEditUiState.Success) {
                                    // the word is not changed
                                    if (editUiState.word.asWordInfo() == it) onBack()
                                    else {
                                        onSave(
                                            editUiState.word.copy(
                                                word = it.word,
                                                dictionaryName = it.dictionaryName,
                                                pronunciation = it.pronunciation,
                                                meanings = it.meanings
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(text = stringResource(coreR.string.core_ui_ok))
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            currentWordInfo?.let {
                FloatingActionButton(onClick = {
                    if (editUiState is WordEditUiState.Success) {
                        showWordTranslationBottomSheet = true
                        onQueryWord(it.word, editUiState.word.language)
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.search_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            currentWordInfo?.let { info ->
                ContentBoard(
                    wordInfo = info,
                    onChange = { currentWordInfo = it }
                )
            }
        }
    }

    if (showWordTranslationBottomSheet) {
        WordTranslationBottomSheet(
            uiState = queryUiState,
            onSelectWordInfo = {
                if (it != null && it.isNotEmpty()) {
                    showWordTranslationBottomSheet = false
                    currentWordInfo = it
                }
            },
            onDismiss = {
                showWordTranslationBottomSheet = false
            }
        )
    }
}

@Composable
private fun ContentBoard(
    wordInfo: WordInfo,
    onChange: (WordInfo) -> Unit
) {
    val pronunciations by remember(wordInfo) { mutableStateOf(wordInfo.getPronunciations()) }

    Column {
        PronunciationPanel(
            pronunciations = pronunciations,
            onAdd = {
                onChange(
                    wordInfo.copyFromUserInput(
                        pronunciations = pronunciations.toMutableList().apply { add(it) })
                )
            },
            onChange = { old, new ->
                onChange(
                    wordInfo.copyFromUserInput(
                        pronunciations = pronunciations.toMutableList()
                            .apply { set(indexOf(old), new) })
                )
            },
            onDelete = {
                onChange(
                    wordInfo.copyFromUserInput(
                        pronunciations = pronunciations.toMutableList().apply { remove(it) })
                )
            }
        )

        val meanings = wordInfo.meanings
        MeaningsPanel(
            meanings = meanings,
            onAdd = {
                onChange(
                    wordInfo.copyFromUserInput(
                        meanings = meanings.toMutableList().apply { add(it) })
                )
            },
            onChange = { old, new ->
                onChange(wordInfo.copyFromUserInput(meanings = meanings.map { if (it == old) new else it }))
            },
            onDelete = { m ->
                onChange(wordInfo.copyFromUserInput(meanings = meanings.filter { it != m }))
            }
        )
    }
}

@Composable
private fun WordPanel(
    word: String,
    onChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        Text(
            text = word,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }

    if (showDialog) {
        NonEmptyTextInputDialog(
            text = word,
            onConfirm = {
                onChange(it)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun MeaningsPanel(
    meanings: List<WordMeaning>,
    onAdd: (WordMeaning) -> Unit,
    onChange: (WordMeaning, WordMeaning) -> Unit,
    onDelete: (WordMeaning) -> Unit,
) {
    var editingItem by remember { mutableStateOf<WordMeaning?>(null) }
    var deletingItem by remember { mutableStateOf<WordMeaning?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn {
        item {
            ListItem(
                headlineContent = { Text(text = stringResource(coreR.string.core_ui_meanings)) },
                trailingContent = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            painter = painterResource(coreR.drawable.add_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null
                        )
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        items(items = meanings) {
            ListItem(
                headlineContent = { Text(text = it.definition) },
                overlineContent = { Text(text = it.partOfSpeechTag) },
                modifier = Modifier.combinedClickable(
                    onClick = { editingItem = it },
                    onLongClick = { deletingItem = it }
                ),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }

    if (showAddDialog) {
        MeaningDialog(
            meaning = WordMeaning("", ""),
            currentPOSTags = meanings.map { it.partOfSpeechTag }.toSet(),
            onConfirm = {
                onAdd(it)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingItem?.let { m ->
        MeaningDialog(
            meaning = m,
            currentPOSTags = meanings.map { it.partOfSpeechTag }.filter { it != m.partOfSpeechTag }
                .toSet(),
            onConfirm = {
                if (m != it) onChange(m, it)
                editingItem = null
            },
            onDismiss = { editingItem = null }
        )
    }

    deletingItem?.let {
        DeleteDialog(
            text = stringResource(
                R.string.word_edit_screen_delete,
                stringResource(coreR.string.core_ui_meanings).lowercase()
            ),
            onDismiss = { deletingItem = null },
            onConfirm = {
                onDelete(it)
                deletingItem = null
            }
        )
    }
}

@Composable
private fun PronunciationPanel(
    pronunciations: List<String>,
    onAdd: (String) -> Unit,
    onDelete: (String) -> Unit,
    onChange: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingItem by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var deletingItem by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        ListItem(
            headlineContent = { Text(text = stringResource(R.string.word_edit_screen_pronunciation)) },
            trailingContent = {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        painter = painterResource(coreR.drawable.add_24dp_000000_fill0_wght400_grad0_opsz24),
                        contentDescription = null
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pronunciations.forEach {
                InputChip(
                    selected = false,
                    onClick = { editingItem = it },
                    label = { Text(text = it) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_24dp_000000_fill0_wght400_grad0_opsz24),
                            contentDescription = null,
                            // since chip intercepts the other click event,
                            // so this is a workaround in ui to delete the pronunciation
                            modifier = Modifier.clickable {
                                deletingItem = it
                            }
                        )
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        NonEmptyTextInputDialog(
            text = "",
            onConfirm = {
                if (!pronunciations.contains(it)) onAdd(it)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    deletingItem?.let {
        DeleteDialog(
            text = stringResource(
                R.string.word_edit_screen_delete,
                stringResource(R.string.word_edit_screen_pronunciation).lowercase()
            ),
            onConfirm = {
                onDelete(it)
                deletingItem = null
            },
            onDismiss = { deletingItem = null }
        )
    }

    editingItem?.let { s ->
        NonEmptyTextInputDialog(
            text = s,
            onConfirm = {
                if (!pronunciations.contains(it)) {
                    onChange(s, it)
                }
                editingItem = null
            },
            onDismiss = { editingItem = null }
        )
    }
}

@Composable
private fun MeaningDialog(
    meaning: WordMeaning,
    currentPOSTags: Set<String>,
    onConfirm: (WordMeaning) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    var posTag by rememberSaveable(meaning.partOfSpeechTag, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = meaning.partOfSpeechTag,
                selection = TextRange(meaning.partOfSpeechTag.length)
            )
        )
    }

    var definition by rememberSaveable(meaning.definition, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = meaning.definition,
                selection = TextRange(meaning.definition.length)
            )
        )
    }

    var isEmpty by remember { mutableStateOf(false) }

    var isTagDuplicate by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (definition.text.isEmpty()) {
                        isEmpty = true
                        return@TextButton
                    }

                    if (currentPOSTags.contains(posTag.text)) {
                        isTagDuplicate = true
                        return@TextButton
                    }

                    onConfirm(
                        WordMeaning(
                            partOfSpeechTag = posTag.text,
                            definition = definition.text
                        )
                    )
                }
            ) {
                Text(text = stringResource(coreR.string.core_ui_confirm))
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = posTag,
                    onValueChange = {
                        posTag = it
                        if (isTagDuplicate) isTagDuplicate = false
                    },
                    label = { Text(text = stringResource(R.string.word_edit_screen_pos_tag)) },
                    isError = isTagDuplicate,
                    supportingText = {
                        AnimatedVisibility(visible = isTagDuplicate) {
                            Text(
                                text = stringResource(
                                    R.string.word_edit_screen_pos_tag_duplicate_error
                                )
                            )
                        }
                    },
                    trailingIcon = {
                        AnimatedVisibility(visible = isTagDuplicate) {
                            Icon(
                                painter = painterResource(coreR.drawable.error_24dp_000000_fill0_wght400_grad0_opsz24),
                                contentDescription = null
                            )
                        }
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = definition,
                    onValueChange = {
                        definition = it
                        if (isEmpty) isEmpty = false
                    },
                    label = { Text(text = stringResource(R.string.word_edit_screen_definition)) },
                    isError = isEmpty,
                    supportingText = {
                        AnimatedVisibility(visible = isEmpty) {
                            Text(
                                text = stringResource(coreR.string.core_ui_empty_error)
                            )
                        }
                    },
                    trailingIcon = {
                        AnimatedVisibility(visible = isEmpty) {
                            Icon(
                                painter = painterResource(coreR.drawable.error_24dp_000000_fill0_wght400_grad0_opsz24),
                                contentDescription = null
                            )
                        }
                    },
                    maxLines = 10,
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        delay(200)
        focusRequester.requestFocus()
    }
}

@Composable
private fun DeleteDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
        title = { Text(text = text) }
    )
}

private fun WordInfo.copyFromUserInput(
    word: String = this.word,
    pronunciations: List<String> = this.getPronunciations(),
    meanings: List<WordMeaning> = this.meanings
) = WordInfo.fromWebOrUser(
    word = word,
    dictionaryName = dictionaryName,
    pronunciations = pronunciations,
    meanings = meanings
)

@Composable
@Preview(showBackground = true)
private fun ContentBoardPreview() {
    ContentBoard(
        wordInfo = wordsTestData[0].asWordInfo(),
        onChange = {}
    )
}