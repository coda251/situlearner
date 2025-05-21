package com.coda.situlearner.feature.word.quiz.sentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatResponse
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.feature.word.quiz.sentence.util.getPrompt
import com.coda.situlearner.infra.chatbot.Chatbot
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class QuizSentenceViewModel(
    private val wordRepository: WordRepository,
    private val aiStateRepository: AiStateRepository,
    private val client: HttpClient,
) : ViewModel() {

    private val _userEvent = MutableSharedFlow<UserEvent>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = getConfigFlow().flatMapLatest {
        when {
            it == null -> flowOf(UiState.NoChatbotError)
            else -> processIntents(it)
        }
    }.stateIn(
        scope = viewModelScope,
        // never stops to make sure the state is retained after navigation
        started = SharingStarted.Lazily,
        initialValue = UiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun processIntents(config: ChatbotConfig): Flow<UiState> =
        _userEvent
            .onStart { emit(UserEvent.None) }
            .flatMapLatest {
                val bot = Chatbot.getInstance(config, client)
                when (it) {
                    UserEvent.None, UserEvent.NextQuiz -> startQuiz(bot)
                    is UserEvent.Submit -> submitAnswer(it, bot)
                    is UserEvent.Retry -> retry(it, bot)
                }
            }

    private fun startQuiz(bot: Chatbot) = flow {
        emit(UiState.Loading)
        val word = getWordFlow().firstOrNull() ?: kotlin.run {
            emit(UiState.NoWordError)
            return@flow
        }

        val initialSession = UiState.ChatSession(
            word = word,
            prompt = ChatMessage(ChatRole.User, getPrompt(word)),
            messages = emptyList(),
            state = ChatSessionState.Loading
        )
        queryBot(initialSession, bot)
    }

    private fun submitAnswer(
        intent: UserEvent.Submit,
        bot: Chatbot
    ) = flow {
        val text = intent.text
        val session = intent.session

        val newSession = session.copy(
            messages = session.messages + ChatMessage(ChatRole.User, text),
        )
        queryBot(newSession, bot)
    }

    private fun retry(
        intent: UserEvent.Retry,
        bot: Chatbot
    ) = flow {
        queryBot(intent.session, bot)
    }

    private suspend fun FlowCollector<UiState.ChatSession>.queryBot(
        session: UiState.ChatSession,
        bot: Chatbot
    ) {
        emit(session.copy(state = ChatSessionState.Loading))
        val response = bot.sendMessage(session.allMessages)
        emit(session.copyFrom(response))
    }

    private fun getWordFlow() = wordRepository.words.map { wordWithContexts ->
        wordWithContexts.map { it.word }
            //.filter { it.proficiency == WordProficiency.Proficient }
            .randomOrNull()
    }

    private fun getConfigFlow() =
        aiStateRepository.aiState.map { it.configs.getOrNull(it.currentIndex) }

    fun submit(
        session: UiState.ChatSession,
        text: String
    ) {
        text.trim().takeIf { it.isNotBlank() }?.let {
            viewModelScope.launch {
                _userEvent.emit(UserEvent.Submit(session, it))
            }
        }
    }

    fun retry(session: UiState.ChatSession) {
        viewModelScope.launch {
            _userEvent.emit(UserEvent.Retry(session))
        }
    }

    fun nextQuiz() {
        viewModelScope.launch {
            _userEvent.emit(UserEvent.NextQuiz)
        }
    }
}

internal sealed interface UserEvent {
    data object None : UserEvent
    data class Submit(
        val session: UiState.ChatSession,
        val text: String
    ) : UserEvent

    data class Retry(val session: UiState.ChatSession) : UserEvent
    data object NextQuiz : UserEvent
}

internal sealed interface UiState {
    data object Loading : UiState
    data object NoWordError : UiState
    data object NoChatbotError : UiState
    data class ChatSession(
        val word: Word,
        val prompt: ChatMessage,
        val messages: List<ChatMessage>,
        val state: ChatSessionState,
    ) : UiState {
        val allMessages: List<ChatMessage>
            get() = listOf(prompt) + messages

        val quizState: QuizState
            get() = when (messages.size) {
                0 -> QuizState.LoadingQuestion
                1 -> QuizState.Question
                2 -> QuizState.LoadingAnswer
                3 -> QuizState.Answer
                else -> QuizState.Other
            }

        fun copyFrom(response: ChatResponse) = when (response) {
            is ChatResponse.Error -> copy(state = ChatSessionState.Error(response.message))
            is ChatResponse.Success -> copy(
                messages = messages + ChatMessage(ChatRole.Bot, response.content),
                state = ChatSessionState.WaitingInput
            )
        }
    }
}

internal enum class QuizState {
    LoadingQuestion,
    Question,
    LoadingAnswer,
    Answer,
    Other
}

internal sealed interface ChatSessionState {
    data object Loading : ChatSessionState
    data class Error(val message: String) : ChatSessionState
    data object WaitingInput : ChatSessionState
}