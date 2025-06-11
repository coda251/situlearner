package com.coda.situlearner.feature.word.quiz.sentence.domain

import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.infra.chatbot.Chatbot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion

internal class QueryChatbotUseCase(
    private val bot: Chatbot
) {
    operator fun invoke(messages: List<ChatMessage>): Flow<ChatResponse> {
        val buf = StringBuilder()
        var status = ChatStatus.Streaming
        var error: String? = null

        return bot.sendMessage(messages)
            .map { delta ->
                delta.content?.let(buf::append)
                status = if (delta.isFinished) ChatStatus.Finished else ChatStatus.Streaming
                error = delta.error

                ChatResponse(
                    content = buf.toString(),
                    status = status,
                    error = error,
                    totalTokens = delta.totalTokens
                )
            }
            .onCompletion {
                // handled by catch
                if (it != null) return@onCompletion

                val errorMessage = when {
                    // caused by client network
                    // a interruption of sse will not throw exception but close the flow "normally"
                    status != ChatStatus.Finished -> "service interruption"
                    status == ChatStatus.Finished && error != null -> error // caused by server
                    else -> null
                }
                if (errorMessage != null) {
                    emit(
                        ChatResponse(
                            content = buf.toString(),
                            status = ChatStatus.Error,
                            error = errorMessage
                        )
                    )
                }
            }
            .catch {
                emit(
                    ChatResponse(
                        content = buf.toString(),
                        status = ChatStatus.Error,
                        error = it.message
                    )
                )
            }
    }
}