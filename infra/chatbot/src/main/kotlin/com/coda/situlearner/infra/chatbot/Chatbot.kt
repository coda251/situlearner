package com.coda.situlearner.infra.chatbot

import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatResponse
import io.ktor.client.HttpClient

interface Chatbot {

    suspend fun sendMessage(messages: List<ChatMessage>): ChatResponse

    companion object {
        fun getInstance(config: ChatbotConfig, client: HttpClient): Chatbot = when (config) {
            is ChatbotConfig.Aliyun -> Aliyun(config.apiKey, config.model, client)
        }
    }
}