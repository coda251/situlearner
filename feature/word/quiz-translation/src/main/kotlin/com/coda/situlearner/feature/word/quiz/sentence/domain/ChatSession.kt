package com.coda.situlearner.feature.word.quiz.sentence.domain

import com.coda.situlearner.core.model.infra.ChatMessage

internal data class ChatSession(
    val messages: List<ChatMessage> = emptyList(),
    val status: ChatStatus = ChatStatus.Idle,
    val streamingBuffer: String = "",
    val totalTokens: Int? = null,
    val error: String? = null,
)

internal enum class ChatStatus { Idle, Connecting, Streaming, Finished, Error }

internal sealed interface ChatIntent {
    data class Submit(
        val text: String,
        val session: ChatSession
    ): ChatIntent

    data class Retry(val session: ChatSession): ChatIntent

    data object Restart: ChatIntent
}

internal data class ChatResponse(
    val content: String,
    val status: ChatStatus = ChatStatus.Idle,
    val error: String? = null,
    val totalTokens: Int? = null
)