package com.coda.situlearner.feature.word.quiz.sentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatResponse
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.feature.word.quiz.sentence.util.getPrompt
import com.coda.situlearner.infra.chatbot.Chatbot
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class QuizSentenceViewModel(
    private val wordRepository: WordRepository,
    private val aiStateRepository: AiStateRepository,
    private val client: HttpClient,
) : ViewModel() {

    private val _sessionUiState = MutableStateFlow<SessionUiState>(SessionUiState.Loading)
    val sessionUiState = _sessionUiState.asStateFlow()
    private val userInputFlow = MutableStateFlow("")

    val initUiState = combine(
        getWordFlow(),
        getConfigFlow()
    ) { word, config ->
        when {
            word == null -> InitUiState.NoWordError
            config == null -> InitUiState.NoChatbotError
            else -> InitUiState.Ready(word, Chatbot.getInstance(config, client))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = InitUiState.Loading
    )

    init {
        updateSessionUiState()
    }

    private fun updateSessionUiState() {
        viewModelScope.launch {
            initUiState.filterIsInstance<InitUiState.Ready>().collectLatest { ready ->
                val (word, bot) = ready
                handlePrompt(word, bot)
                handleUserInput(bot)
            }
        }
    }

    private suspend fun handlePrompt(word: Word, bot: Chatbot) {
        val promptMessages = listOf(getPrompt(word).asUserChatMessage())
        val promptResponse = bot.sendMessage(promptMessages)
        val initialSession = SessionUiState.Result(promptMessages, ChatSessionState.Loading)
        _sessionUiState.update { initialSession.copyFrom(promptResponse) }
    }

    private suspend fun handleUserInput(bot: Chatbot) {
        userInputFlow.map { it.trim() }.filter { it.isNotBlank() }.collectLatest { input ->
            val currentSession =
                (_sessionUiState.value as? SessionUiState.Result)?.let {
                    val userMessage = input.asUserChatMessage()
                    SessionUiState.Result(
                        messages = it.messages + userMessage,
                        state = ChatSessionState.Loading
                    )
                } ?: return@collectLatest

            _sessionUiState.update { currentSession }
            val response = bot.sendMessage(currentSession.messages)
            _sessionUiState.update { currentSession.copyFrom(response) }
        }
    }

    private fun String.asUserChatMessage() = ChatMessage(ChatRole.User, this)

    private fun getWordFlow() = wordRepository.words.map { wordWithContexts ->
        wordWithContexts.map { it.word }
            .filter { it.proficiency == WordProficiency.Proficient }
            .randomOrNull()
    }

    private fun getConfigFlow() =
        aiStateRepository.aiState.map { it.configs.getOrNull(it.currentIndex) }

    fun submit(text: String) {
        userInputFlow.value = text
    }
}

internal sealed interface InitUiState {
    data object Loading : InitUiState
    data object NoWordError : InitUiState
    data object NoChatbotError : InitUiState
    data class Ready(
        val word: Word,
        val bot: Chatbot
    ) : InitUiState
}

internal sealed interface SessionUiState {
    data object Loading : SessionUiState
    data class Result(
        val messages: List<ChatMessage>,
        val state: ChatSessionState,
    ) : SessionUiState {
        fun copyFrom(response: ChatResponse) = when (response) {
            is ChatResponse.Error -> copy(state = ChatSessionState.Error(response.message))
            is ChatResponse.Success -> copy(
                messages = messages + ChatMessage(ChatRole.Bot, response.content),
                state = ChatSessionState.WaitingInput
            )
        }
    }
}

internal sealed interface ChatSessionState {
    data object Loading : ChatSessionState
    data class Error(val message: String) : ChatSessionState
    data object WaitingInput : ChatSessionState
}