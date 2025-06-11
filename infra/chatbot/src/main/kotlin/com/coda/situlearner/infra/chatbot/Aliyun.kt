package com.coda.situlearner.infra.chatbot

import com.coda.situlearner.core.model.infra.ChatDelta
import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

internal class Aliyun(
    private val apiKey: String,
    private val model: String,
    private val client: HttpClient,
) : Chatbot {

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    override fun sendMessage(messages: List<ChatMessage>): Flow<ChatDelta> = flow {
        val body = buildJsonObject {
            put("model", model)
            put("stream", true)
            putJsonObject("stream_options") { put("include_usage", true) }
            putJsonArray("messages") {
                messages.forEach { msg ->
                    addJsonObject {
                        put("role", msg.role.asApiValue())
                        put("content", msg.content)
                    }
                }
            }
        }

        client.sse(
            urlString = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions",
            request = {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                bearerAuth(apiKey)
                accept(ContentType.Text.EventStream)
                setBody(body)
            }
        ) {
            var error: String? = null

            incoming.mapNotNull { it.data }.collect { data ->
                if (data == "[DONE]") return@collect
                val chunk = json.decodeFromString<AliyunChunk>(data)
                val choice = chunk.choices.firstOrNull()

                error = if (chunk.usage?.totalTokens != null) error // avoid overriding error msg
                else choice?.finishReason?.takeIf { it != "stop" }

                emit(
                    ChatDelta(
                        content = choice?.delta?.content,
                        isFinished = choice?.finishReason != null || chunk.usage?.totalTokens != null,
                        error = error,
                        totalTokens = chunk.usage?.totalTokens
                    )
                )
            }
        }
    }.flowOn(Dispatchers.IO)
}

private fun ChatRole.asApiValue(): String = when (this) {
    ChatRole.User -> "user"
    ChatRole.Bot -> "assistant"
}

@kotlinx.serialization.Serializable
private data class AliyunChunk(
    val id: String,
    val created: Long,
    @SerialName("object") val obj: String,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage?
) {
    @kotlinx.serialization.Serializable
    data class Choice(
        val index: Int,
        @SerialName("finish_reason") val finishReason: String?,
        val delta: Delta
    )

    @kotlinx.serialization.Serializable
    data class Delta(
        val content: String,
        val role: String? = null,
    )

    @kotlinx.serialization.Serializable
    data class Usage(
        @SerialName("prompt_tokens") val promptTokens: Int,
        @SerialName("completion_tokens") val completionTokens: Int,
        @SerialName("total_tokens") val totalTokens: Int
    )
}