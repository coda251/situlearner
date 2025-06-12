package com.coda.situlearner.feature.home.settings.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coda.situlearner.core.model.data.Aliyun
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.ChatbotConfigList
import com.coda.situlearner.core.model.data.ChatbotType
import com.coda.situlearner.core.ui.widget.BackButton
import com.coda.situlearner.feature.home.settings.chatbot.model.ChatbotItem
import com.coda.situlearner.feature.home.settings.chatbot.model.asAiState
import com.coda.situlearner.feature.home.settings.chatbot.model.asChatbotItem
import org.koin.androidx.compose.koinViewModel
import com.coda.situlearner.core.ui.R as coreR

@Composable
internal fun SettingsChatbotScreen(
    onBack: () -> Unit,
    viewModel: SettingsChatbotViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsChatbotScreen(
        uiState = uiState,
        onBack = onBack,
        onSetAiState = viewModel::setChatbotConfigList,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsChatbotScreen(
    uiState: ChatbotUiState,
    onBack: () -> Unit,
    onSetAiState: (ChatbotConfigList) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            when (uiState) {
                is ChatbotUiState.Success -> {
                    ContentBoard(
                        chatbots = uiState.chatbots,
                        onAddOrEditConfig = { onSetAiState(uiState.chatbots.asAiState(it)) },
                        onSetCurrentType = { onSetAiState(uiState.chatbots.asAiState(it)) }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun ContentBoard(
    chatbots: List<ChatbotItem>,
    onAddOrEditConfig: (ChatbotConfig) -> Unit,
    onSetCurrentType: (ChatbotType) -> Unit,
) {
    LazyColumn {
        items(
            items = chatbots,
            key = { it.type }
        ) {
            ChatbotItemView(
                item = it,
                onAddOrEditConfig = onAddOrEditConfig,
                onSetCurrentType = onSetCurrentType
            )
        }
    }
}

@Composable
private fun ChatbotItemView(
    item: ChatbotItem,
    onAddOrEditConfig: (ChatbotConfig) -> Unit,
    onSetCurrentType: (ChatbotType) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    ListItem(
        leadingContent = {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null
            )
        },
        headlineContent = {
            Text(
                text = item.type.botName,
                color = if (item.status == ChatbotItem.Status.Unregistered) MaterialTheme.colorScheme.onSurface.copy(
                    0.38f
                )
                else Color.Unspecified
            )
        },
        supportingContent = item.modelName?.let {
            {
                Text(text = it)
            }
        },
        trailingContent = item.status.takeIf { it == ChatbotItem.Status.Active }?.let {
            {
                Text(
                    text = stringResource(R.string.home_settings_chatbot_screen_current),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = {
                showDialog = true
            },
            onDoubleClick = {
                item.cfg?.type?.let(onSetCurrentType)
            }
        )
    )

    if (showDialog) {
        ChatbotConfigDialog(
            item = item,
            onConfirm = {
                showDialog = false
                onAddOrEditConfig(it)
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun ChatbotConfigDialog(
    item: ChatbotItem,
    onConfirm: (ChatbotConfig) -> Unit,
    onDismiss: () -> Unit,
) {
    when (item.type) {
        ChatbotType.Aliyun -> {
            AliyunConfigDialog(
                item = item.cfg as? Aliyun ?: Aliyun.Default,
                onConfirm = onConfirm,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun AliyunConfigDialog(
    item: Aliyun,
    onConfirm: (Aliyun) -> Unit,
    onDismiss: () -> Unit,
) {
    var apiKey by rememberSaveable(item.apiKey, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = item.apiKey,
                selection = TextRange(item.apiKey.length)
            )
        )
    }

    var model by rememberSaveable(item.model, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = item.model,
                selection = TextRange(item.apiKey.length)
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        Aliyun(
                            apiKey = apiKey.text,
                            model = model.text
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
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = {
                        Text(
                            text = stringResource(R.string.home_settings_chatbot_screen_api_key)
                        )
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    enabled = false,
                    label = {
                        Text(
                            text = stringResource(R.string.home_settings_chatbot_screen_model)
                        )
                    }
                )
            }
        }
    )
}

@Preview
@Composable
private fun SettingsChatbotScreenPreview() {
    val uiState = ChatbotUiState.Success(
        chatbots = ChatbotType.entries.map {
            it.asChatbotItem().copy(status = ChatbotItem.Status.Active)
        }
    )

    SettingsChatbotScreen(
        uiState = uiState,
        onBack = {},
        onSetAiState = {},
    )
}