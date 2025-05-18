package com.coda.situlearner.feature.home.settings.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.model.data.AiState
import com.coda.situlearner.core.model.data.ChatbotType
import com.coda.situlearner.feature.home.settings.chatbot.model.ChatbotItem
import com.coda.situlearner.feature.home.settings.chatbot.model.asChatbotItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsChatbotViewModel(
    private val aiStateRepository: AiStateRepository
) : ViewModel() {

    val uiState = aiStateRepository.aiState.map { state ->
        val typeToConfig = state.configs.associateBy { it.type }
        val selectedType = state.configs.getOrNull(state.currentIndex)?.type

        val items = ChatbotType.entries.map { type ->
            typeToConfig[type]?.asChatbotItem(isSelect = type == selectedType)
                ?: type.asChatbotItem()
        }.sortedBy {
            it.status.level
        }

        ChatbotUiState.Success(items)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatbotUiState.Loading
    )

    fun setAiState(aiState: AiState) {
        viewModelScope.launch {
            aiStateRepository.setAiState(aiState)
        }
    }
}

internal sealed interface ChatbotUiState {
    data object Loading : ChatbotUiState
    data class Success(val chatbots: List<ChatbotItem>) : ChatbotUiState
}