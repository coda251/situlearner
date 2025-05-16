package com.coda.situlearner.infra.chatbot

import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatResponse
import com.coda.situlearner.core.model.infra.ChatRole
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal class Aliyun(
    private val apiKey: String,
    private val model: String,
    private val client: HttpClient,
) : Chatbot {

    override suspend fun sendMessage(messages: List<ChatMessage>): ChatResponse =
        withContext(Dispatchers.IO) {
            try {
                val response: AliyunResponse =
                    client.post("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions") {
                        header("Content-Type", "application/json")
                        header("Authorization", "Bearer $apiKey")
                        setBody(
                            buildJsonObject {
                                put("model", model)
                                putJsonArray("messages") {
                                    messages.forEach { msg ->
                                        addJsonObject {
                                            put("role", msg.role.asApiValue())
                                            put("content", msg.content)
                                        }
                                    }
                                }
                            }
                        )
                    }.body()

                response.asChatResponse()
            } catch (e: ResponseException) {
                ChatResponse.Error(e.response.status.value, e.message ?: "Response Error")
            } catch (e: Throwable) {
                ChatResponse.Error(message = e.message ?: "Unexpected error")
            }
        }
}

private fun ChatRole.asApiValue(): String = when (this) {
    ChatRole.User -> "user"
    ChatRole.Bot -> "assistant"
}

@kotlinx.serialization.Serializable
private data class AliyunResponse(
    val id: String,
    val created: Long,
    @SerialName("object") val obj: String,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
) {
    @kotlinx.serialization.Serializable
    data class Choice(
        val index: Int,
        @SerialName("finish_reason") val finishReason: String,
        val message: Message
    )

    @kotlinx.serialization.Serializable
    data class Message(
        val role: String,
        val content: String
    )

    @kotlinx.serialization.Serializable
    data class Usage(
        @SerialName("prompt_tokens") val promptTokens: Int,
        @SerialName("completion_tokens") val completionTokens: Int,
        @SerialName("total_tokens") val totalTokens: Int
    )
}

private fun AliyunResponse.asChatResponse() =
    ChatResponse.Success(choices.firstOrNull()?.message?.content.orEmpty())