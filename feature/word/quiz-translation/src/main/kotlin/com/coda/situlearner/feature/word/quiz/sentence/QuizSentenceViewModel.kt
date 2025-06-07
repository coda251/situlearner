package com.coda.situlearner.feature.word.quiz.sentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.feature.UserRating
import com.coda.situlearner.core.model.feature.mapper.toWordProficiency
import com.coda.situlearner.core.model.feature.mapper.updateWith
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatResponse
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.feature.word.quiz.sentence.util.getReviewPrompt
import com.coda.situlearner.infra.chatbot.Chatbot
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

internal class QuizSentenceViewModel(
    private val wordRepository: WordRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    aiStateRepository: AiStateRepository,
    private val client: HttpClient,
) : ViewModel() {

    private val _userEvent = MutableSharedFlow<UserEvent>()

    private val _evaluateEvent = MutableSharedFlow<EvaluateEvent>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = aiStateRepository.aiState.flatMapLatest {
        val chatbotConfig = it.configs.currentItem
        val quizPromptTemplate = it.promptTemplate
        when {
            chatbotConfig == null -> flowOf(UiState.NoChatbotError)
            else -> {
                val chatbot = Chatbot.getInstance(chatbotConfig, client)
                processEvents(chatbot, quizPromptTemplate)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        // never stops to make sure the state is retained after navigation
        started = SharingStarted.Lazily,
        initialValue = UiState.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val evaluateState = uiState
        .filterIsInstance(UiState.ChatSession::class)
        .filter { it.hasUserAnswer }
        .map { it.word }
        .distinctUntilChanged().flatMapLatest {
            processEvaluateEvents(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = EvaluateState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun processEvaluateEvents(word: Word): Flow<EvaluateState> =
        _evaluateEvent
            .onStart { emit(EvaluateEvent.None) }
            .flatMapLatest {
                when (it) {
                    EvaluateEvent.None -> flowOf(EvaluateState.Prepared(word))
                    is EvaluateEvent.Submit -> flowOf(it.state)
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun processEvents(
        bot: Chatbot,
        template: TranslationQuizPromptTemplate,
    ): Flow<UiState> =
        _userEvent
            .onStart { emit(UserEvent.None) }
            .flatMapLatest {
                when (it) {
                    UserEvent.None, UserEvent.NextQuiz -> startQuiz(bot, template)
                    is UserEvent.Submit -> submitAnswer(it, bot)
                    is UserEvent.Retry -> retry(it, bot)
                }
            }

    private fun startQuiz(
        bot: Chatbot,
        template: TranslationQuizPromptTemplate
    ) = flow {
        emit(UiState.Loading)
        val word = getWord() ?: kotlin.run {
            emit(UiState.NoWordError)
            return@flow
        }

        val initialSession = UiState.ChatSession(
            word = word,
            prompt = template.buildPrompt(word),
            quizState = QuizState.LoadingQuestion,
            sessionState = ChatSessionState.Loading
        )
        queryBot(initialSession, bot)
    }

    private fun submitAnswer(
        intent: UserEvent.Submit,
        bot: Chatbot
    ) = flow {
        when (val quizState = intent.session.quizState) {
            is QuizState.Question -> {
                val newSession =
                    intent.session.copy(
                        quizState = QuizState.Answer(
                            question = quizState.question,
                            userAnswer = intent.text
                        )
                    )
                queryBot(newSession, bot)
            }

            else -> return@flow
        }
    }

    private fun retry(
        intent: UserEvent.Retry,
        bot: Chatbot
    ) = flow {
        val quizState = intent.session.quizState
        when (quizState) {
            QuizState.LoadingQuestion, is QuizState.Answer -> {
                queryBot(intent.session, bot)
            }

            else -> return@flow
        }
    }

    private suspend fun FlowCollector<UiState.ChatSession>.queryBot(
        session: UiState.ChatSession,
        bot: Chatbot
    ) {
        session.chatbotMessages?.let {
            emit(session.copy(sessionState = ChatSessionState.Loading))
            val response = bot.sendMessage(it)
            emit(session.copyFrom(response))
        }
    }

    private suspend fun getWord(): Word? {
        val language = userPreferenceRepository.userPreference.firstOrNull()?.wordLibraryLanguage
            ?: return null

        return wordRepository.getTranslationQuizWord(
            language,
            Clock.System.now()
        )
    }

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

    fun evaluate(
        session: UiState.ChatSession,
        state: EvaluateState
    ) {
        viewModelScope.launch {
            _evaluateEvent.emit(EvaluateEvent.Submit(state))
            val questionAndUserAnswer = session.questionAndUserAnswer
            if (state is EvaluateState.Result && questionAndUserAnswer != null) {
                val (question, answer) = questionAndUserAnswer

                val quizInfo = (wordRepository.getTranslationQuizStats(state.wordId)?.copy(
                    lastQuestion = question,
                    userAnswer = answer
                ) ?: TranslationQuizStats(
                    wordId = state.wordId,
                    easeFactor = 2.5,
                    intervalDays = 1,
                    nextQuizDate = Clock.System.now(),
                    lastQuestion = question,
                    userAnswer = answer
                )).updateWith(state.toUserRating())

                wordRepository.upsertTranslationQuizStats(quizInfo)
                wordRepository.updateWord(
                    session.word.copy(
                        translationProficiency = quizInfo.toWordProficiency()
                    )
                )
            }
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

internal sealed interface EvaluateEvent {
    data object None : EvaluateEvent
    data class Submit(val state: EvaluateState) : EvaluateEvent
}

internal sealed interface UiState {
    data object Loading : UiState
    data object NoWordError : UiState
    data object NoChatbotError : UiState
    data class ChatSession(
        val word: Word,
        val prompt: String,
        val quizState: QuizState,
        val sessionState: ChatSessionState,
    ) : UiState {

        val displayedMessages: List<ChatMessage>
            get() = when (quizState) {
                QuizState.LoadingQuestion -> emptyList()
                is QuizState.Question -> listOf(
                    ChatMessage(
                        role = ChatRole.Bot,
                        content = quizState.question
                    )
                )

                is QuizState.Answer -> listOf(
                    ChatMessage(role = ChatRole.Bot, content = quizState.question),
                    ChatMessage(role = ChatRole.User, content = quizState.userAnswer),
                )

                is QuizState.Review -> listOf(
                    ChatMessage(role = ChatRole.Bot, content = quizState.question),
                    ChatMessage(role = ChatRole.User, content = quizState.userAnswer),
                    ChatMessage(role = ChatRole.Bot, content = quizState.botReview),
                )
            }

        val chatbotMessages: List<ChatMessage>?
            get() = when (quizState) {
                QuizState.LoadingQuestion -> listOf(
                    ChatMessage(ChatRole.Bot, prompt)
                )

                is QuizState.Answer -> listOf(
                    ChatMessage(
                        ChatRole.User, getReviewPrompt(
                            word = word,
                            question = quizState.question,
                            userAnswer = quizState.userAnswer,
                        )
                    ),
                )

                else -> null
            }

        val hasUserAnswer: Boolean
            get() = quizState is QuizState.Answer || quizState is QuizState.Review

        val questionAndUserAnswer: Pair<String, String>?
            get() = when (quizState) {
                QuizState.LoadingQuestion -> null
                is QuizState.Question -> null
                is QuizState.Answer -> quizState.question to quizState.userAnswer
                is QuizState.Review -> quizState.question to quizState.userAnswer
            }


        fun copyFrom(response: ChatResponse) = when (response) {
            is ChatResponse.Error -> copy(sessionState = ChatSessionState.Error(response.message))
            is ChatResponse.Success -> copy(
                quizState = when (quizState) {
                    QuizState.LoadingQuestion -> QuizState.Question(response.content)
                    is QuizState.Answer -> QuizState.Review(
                        question = quizState.question,
                        userAnswer = quizState.userAnswer,
                        botReview = response.content
                    )

                    else -> quizState
                },
                sessionState = ChatSessionState.WaitingInput
            )
        }
    }
}

internal sealed interface QuizState {
    data object LoadingQuestion : QuizState
    data class Question(val question: String) : QuizState
    data class Answer(
        val question: String,
        val userAnswer: String
    ) : QuizState

    data class Review(
        val question: String,
        val userAnswer: String,
        val botReview: String
    ) : QuizState
}

internal sealed interface EvaluateState {
    data object Loading : EvaluateState
    data class Prepared(val word: Word) : EvaluateState
    data class UsageEvaluated(
        val wordId: String,
        val isWordUsed: Boolean
    ) : EvaluateState

    data class Result(
        val wordId: String,
        val isWordUsed: Boolean,
        val isCorrectOrRecalled: Boolean,
    ) : EvaluateState {
        fun toUserRating(): UserRating = when {
            isWordUsed && isCorrectOrRecalled -> UserRating.Easy
            isWordUsed && !isCorrectOrRecalled -> UserRating.Good
            !isWordUsed && isCorrectOrRecalled -> UserRating.Hard
            else -> UserRating.Again
        }
    }
}

internal sealed interface ChatSessionState {
    data object Loading : ChatSessionState
    data class Error(val message: String) : ChatSessionState
    data object WaitingInput : ChatSessionState
}