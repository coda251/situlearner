package com.coda.situlearner.feature.word.quiz.sentence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coda.situlearner.core.data.repository.AiStateRepository
import com.coda.situlearner.core.data.repository.UserPreferenceRepository
import com.coda.situlearner.core.data.repository.WordRepository
import com.coda.situlearner.core.model.data.TranslationEvalBackend
import com.coda.situlearner.core.model.data.TranslationEvalPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizStats
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.feature.UserRating
import com.coda.situlearner.core.model.feature.mapper.toWordProficiency
import com.coda.situlearner.core.model.feature.mapper.updateWith
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import com.coda.situlearner.feature.word.quiz.sentence.domain.ChatIntent
import com.coda.situlearner.feature.word.quiz.sentence.domain.ChatSession
import com.coda.situlearner.feature.word.quiz.sentence.domain.ChatStatus
import com.coda.situlearner.feature.word.quiz.sentence.domain.GetChatSessionUseCase
import com.coda.situlearner.feature.word.quiz.sentence.domain.QueryChatbotUseCase
import com.coda.situlearner.infra.chatbot.Chatbot
import io.ktor.client.HttpClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emitAll
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class QuizSentenceViewModel(
    private val wordRepository: WordRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    aiStateRepository: AiStateRepository,
    private val client: HttpClient,
) : ViewModel() {

    private val _quizEvent = MutableSharedFlow<QuizEvent>()

    private val _evaluateEvent = MutableSharedFlow<EvaluateEvent>()

    val quizState = aiStateRepository.aiState.flatMapLatest {
        val chatbotConfig = it.configs.currentItem
        when {
            chatbotConfig == null -> flowOf(QuizUiState.NoChatbotError)
            else -> {
                val chatbot = Chatbot.getInstance(chatbotConfig, client)
                val getChatSessionUseCase = GetChatSessionUseCase(QueryChatbotUseCase(chatbot))
                processQuizEvents(
                    getChatSessionUseCase,
                    it.quizPromptTemplate,
                    it.evalPromptTemplate,
                    it.evalBackend,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        // never stops to make sure the state is retained after navigation
        started = SharingStarted.Lazily,
        initialValue = QuizUiState.Loading
    )

    val evaluateState = quizState
        .filterIsInstance(QuizUiState.Data::class)
        .filter { it.hasUserAnswer }
        .distinctUntilChangedBy { it.word.id }
        .map { Triple(it.word, it.question, it.userAnswer) }
        .flatMapLatest { processEvaluateEvents(it.first, it.second, it.third) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = EvaluateState.Idle
        )

    private fun processEvaluateEvents(
        word: Word,
        question: String,
        answer: String
    ): Flow<EvaluateState> =
        _evaluateEvent
            .onStart { emit(EvaluateEvent.Start) }
            .flatMapLatest {
                when (it) {
                    EvaluateEvent.Start -> flowOf(EvaluateState.Data(word, question, answer))
                    is EvaluateEvent.Submit -> evaluateQuiz(it.state, it.evaluation)
                }
            }

    private fun processQuizEvents(
        useCase: GetChatSessionUseCase,
        quizTemplate: TranslationQuizPromptTemplate,
        evalTemplate: TranslationEvalPromptTemplate,
        reviewBackend: TranslationEvalBackend,
    ): Flow<QuizUiState> =
        _quizEvent
            .onStart { emit(QuizEvent.Start) }
            .flatMapLatest {
                when (it) {
                    QuizEvent.Start, QuizEvent.NextQuiz -> startQuiz(
                        useCase,
                        quizTemplate,
                        evalTemplate,
                        reviewBackend
                    )

                    is QuizEvent.Submit -> submitAnswer(useCase, it)
                    is QuizEvent.Retry -> retry(useCase, it)
                }
            }

    private fun startQuiz(
        useCase: GetChatSessionUseCase,
        quizTemplate: TranslationQuizPromptTemplate,
        evalTemplate: TranslationEvalPromptTemplate,
        reviewBackend: TranslationEvalBackend,
    ) = flow {
        val word = getWord() ?: kotlin.run {
            emit(QuizUiState.NoWordError)
            return@flow
        }

        val state = QuizUiState.Data(
            word = word,
            questionTemplate = quizTemplate,
            reviewTemplate = evalTemplate,
            reviewBackend = reviewBackend
        )
        emitAll(queryBotFlow(useCase, state))
    }

    private fun submitAnswer(
        useCase: GetChatSessionUseCase,
        intent: QuizEvent.Submit
    ) = flow {
        var state = intent.state
        if (state.phase != QuizPhase.Question) return@flow

        state = state.copy(
            userAnswer = intent.text,
            phase = QuizPhase.Answer
        )
        emit(state)

        if (state.reviewBackend == TranslationEvalBackend.UseBuiltinChatbot) {
            emitAll(queryBotFlow(useCase, state))
        }
    }

    private fun retry(
        useCase: GetChatSessionUseCase,
        intent: QuizEvent.Retry,
    ) = queryBotFlow(useCase, intent.state)

    private fun queryBotFlow(
        useCase: GetChatSessionUseCase,
        state: QuizUiState.Data
    ): Flow<QuizUiState.Data> {
        val userQuery = state.query ?: return flowOf(state)

        val nextQuizPhase =
            if (state.phase == QuizPhase.Idle) QuizPhase.Question else QuizPhase.Review

        return useCase(ChatIntent.Submit(userQuery, ChatSession()))
            .map {
                when (it.status) {
                    ChatStatus.Idle, ChatStatus.Connecting -> state.copy(
                        chatStatus = ChatStatus.Connecting,
                        partial = ""
                    )

                    ChatStatus.Streaming -> state.copy(
                        chatStatus = ChatStatus.Streaming,
                        partial = it.streamingBuffer
                    )

                    ChatStatus.Finished -> state.copy(
                        phase = nextQuizPhase,
                        question = if (nextQuizPhase == QuizPhase.Question) it.messages.last().content
                        else state.question,
                        botReview = if (nextQuizPhase == QuizPhase.Review) it.messages.last().content
                        else state.botReview,
                        chatStatus = ChatStatus.Finished,
                        partial = "",
                        totalTokens = state.totalTokens?.let { currentTokens ->
                            if (it.totalTokens == null) currentTokens
                            else currentTokens + it.totalTokens
                        } ?: it.totalTokens
                    )

                    ChatStatus.Error -> state.copy(
                        chatStatus = ChatStatus.Error,
                        partial = it.streamingBuffer,
                        error = it.error
                    )
                }
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

    private suspend fun updateTranslationQuizStats(state: EvaluateState.Data) {
        val quizInfo = (wordRepository.getTranslationQuizStats(state.word.id)?.copy(
            lastQuestion = state.question,
            userAnswer = state.userAnswer
        ) ?: TranslationQuizStats(
            wordId = state.word.id,
            easeFactor = 2.5,
            intervalDays = 1,
            nextQuizDate = Clock.System.now(),
            lastQuestion = state.question,
            userAnswer = state.userAnswer
        )).updateWith(state.toUserRating())

        wordRepository.upsertTranslationQuizStats(quizInfo)
        wordRepository.updateWord(
            state.word.copy(
                translationProficiency = quizInfo.toWordProficiency()
            )
        )
    }

    private fun evaluateQuiz(
        state: EvaluateState.Data,
        evaluation: Boolean
    ): Flow<EvaluateState> = flow {
        val nextState = when (state.phase) {
            EvaluatePhase.Existence -> {
                state.copy(
                    isWordUsed = evaluation,
                    phase = if (evaluation) EvaluatePhase.Usage else EvaluatePhase.Recall
                )
            }

            EvaluatePhase.Usage, EvaluatePhase.Recall -> {
                state.copy(
                    isCorrectOrRecalled = evaluation,
                    phase = EvaluatePhase.Done
                )
            }

            EvaluatePhase.Done -> state
        }

        emit(nextState)

        // side effect
        if (nextState.phase == EvaluatePhase.Done) {
            updateTranslationQuizStats(nextState)
        }
    }

    fun submit(
        state: QuizUiState.Data,
        text: String
    ) {
        text.trim().takeIf { it.isNotBlank() }?.let {
            viewModelScope.launch {
                _quizEvent.emit(QuizEvent.Submit(state, it))
            }
        }
    }

    fun retry(state: QuizUiState.Data) {
        viewModelScope.launch {
            _quizEvent.emit(QuizEvent.Retry(state))
        }
    }

    fun nextQuiz() {
        viewModelScope.launch {
            _quizEvent.emit(QuizEvent.NextQuiz)
        }
    }

    fun evaluate(
        state: EvaluateState.Data,
        evaluation: Boolean
    ) {
        viewModelScope.launch {
            _evaluateEvent.emit(EvaluateEvent.Submit(state, evaluation))
        }
    }
}

internal sealed interface QuizEvent {
    data object Start : QuizEvent
    data class Submit(
        val state: QuizUiState.Data,
        val text: String
    ) : QuizEvent

    data class Retry(val state: QuizUiState.Data) : QuizEvent
    data object NextQuiz : QuizEvent
}

internal sealed interface EvaluateEvent {
    data object Start : EvaluateEvent
    data class Submit(
        val state: EvaluateState.Data,
        val evaluation: Boolean
    ) : EvaluateEvent
}

internal sealed interface EvaluateState {
    data object Idle : EvaluateState
    data class Data(
        val word: Word,
        val question: String,
        val userAnswer: String,
        val isWordUsed: Boolean = false,
        val isCorrectOrRecalled: Boolean = false,
        val phase: EvaluatePhase = EvaluatePhase.Existence,
    ) : EvaluateState {

        fun toUserRating(): UserRating = when {
            isWordUsed && isCorrectOrRecalled -> UserRating.Easy
            isWordUsed && !isCorrectOrRecalled -> UserRating.Good
            !isWordUsed && isCorrectOrRecalled -> UserRating.Hard
            else -> UserRating.Again
        }
    }
}

internal enum class EvaluatePhase {
    Existence, Usage, Recall, Done
}

internal sealed interface QuizUiState {
    data object Loading : QuizUiState
    data object NoWordError : QuizUiState
    data object NoChatbotError : QuizUiState
    data class Data(
        val word: Word,
        val questionTemplate: TranslationQuizPromptTemplate,
        val reviewTemplate: TranslationEvalPromptTemplate,
        val reviewBackend: TranslationEvalBackend,

        // quiz related
        val phase: QuizPhase = QuizPhase.Idle,
        val question: String = "",
        val userAnswer: String = "",
        val botReview: String = "",

        // connection related
        val chatStatus: ChatStatus = ChatStatus.Idle,
        val partial: String = "",
        val error: String? = null,
        val totalTokens: Int? = null,
    ) : QuizUiState {
        private val ableToChat: Boolean
            get() = chatStatus == ChatStatus.Idle || chatStatus == ChatStatus.Error || chatStatus == ChatStatus.Finished

        private val shouldQueryQuestion: Boolean
            get() = phase == QuizPhase.Idle && ableToChat

        private val shouldQueryReview: Boolean
            get() = phase == QuizPhase.Answer && ableToChat

        val hasUserAnswer: Boolean
            get() = phase == QuizPhase.Answer || phase == QuizPhase.Review

        // choose no history message
        val query: String?
            get() = when {
                shouldQueryQuestion -> questionTemplate.buildPrompt(word)
                shouldQueryReview -> reviewTemplate.buildPrompt(word, question, userAnswer)
                else -> null
            }

        val displayedMessages: List<ChatMessage>
            get() = when (phase) {
                QuizPhase.Idle -> emptyList()
                QuizPhase.Question -> listOf(
                    ChatMessage(role = ChatRole.Bot, content = question)
                )

                QuizPhase.Answer -> listOf(
                    ChatMessage(role = ChatRole.Bot, content = question),
                    ChatMessage(role = ChatRole.User, content = userAnswer)
                )

                QuizPhase.Review -> listOf(
                    ChatMessage(role = ChatRole.Bot, content = question),
                    ChatMessage(role = ChatRole.User, content = userAnswer),
                    ChatMessage(role = ChatRole.Bot, content = botReview)
                )
            }
    }
}

internal enum class QuizPhase { Idle, Question, Answer, Review }