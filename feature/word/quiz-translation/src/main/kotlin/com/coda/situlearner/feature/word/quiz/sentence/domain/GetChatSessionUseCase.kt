package com.coda.situlearner.feature.word.quiz.sentence.domain

import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.runningFold

internal class GetChatSessionUseCase(
    private val queryBot: QueryChatbotUseCase
) {
    operator fun invoke(intent: ChatIntent): Flow<ChatSession> = when (intent) {
        ChatIntent.Restart -> flowOf(
            ChatSession(
                messages = emptyList(),
                status = ChatStatus.Idle
            )
        )

        is ChatIntent.Submit -> {
            val userMsg = ChatMessage(ChatRole.User, intent.text)
            val requestMsg = intent.session.messages + userMsg
            queryBot(requestMsg).toChatSessionFlow(
                initial = intent.session.copy(
                    messages = requestMsg,
                    status = ChatStatus.Connecting,
                    streamingBuffer = ""
                )
            )
        }

        is ChatIntent.Retry -> {
            val reconnect = intent.session.copy(
                status = ChatStatus.Connecting,
                streamingBuffer = ""
            )
            queryBot(intent.session.messages).toChatSessionFlow(reconnect)
        }
    }
}

private fun Flow<ChatResponse>.toChatSessionFlow(
    initial: ChatSession
): Flow<ChatSession> = runningFold(initial) { cur, r ->
    when(r.status) {
        ChatStatus.Error ->
            cur.copy(
                status = ChatStatus.Error,
                error = r.error
            )

        ChatStatus.Finished ->
            cur.copy(
                messages = cur.messages +
                        ChatMessage(ChatRole.Bot, r.content),
                status = ChatStatus.Finished,
                totalTokens = r.totalTokens,
                streamingBuffer = ""
            )

        else ->
            cur.copy(
                status = ChatStatus.Streaming,
                streamingBuffer = r.content,
            )
    }
}