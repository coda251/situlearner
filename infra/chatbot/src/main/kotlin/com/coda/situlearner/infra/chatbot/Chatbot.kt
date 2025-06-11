package com.coda.situlearner.infra.chatbot

import com.coda.situlearner.core.model.data.Aliyun
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.infra.ChatDelta
import com.coda.situlearner.core.model.infra.ChatMessage
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow

interface Chatbot {

    fun sendMessage(messages: List<ChatMessage>): Flow<ChatDelta>

    companion object {
        fun getInstance(config: ChatbotConfig, client: HttpClient): Chatbot = when (config) {
            is Aliyun -> Aliyun(config.apiKey, config.model, client)
        }
    }
}