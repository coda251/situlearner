package com.coda.situlearner.infra.chatbot

import com.coda.situlearner.core.model.infra.ChatMessage
import com.coda.situlearner.core.model.infra.ChatRole
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test

class AliyunTest {

    // NOTE: modify run configuration in junit to change the api key
    private val apiKey = System.getenv()["ALIYUN_API_KEY"] ?: error("ALIYUN_API_KEY is not set")
    private val chatbot = Aliyun(
        apiKey = apiKey,
        model = "deepseek-v3",
        client = HttpClient(CIO) {
            install(SSE)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    )

    @Test
    fun `test Aliyun`() = runTest {
        val message = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = "你好"
            )
        )

        chatbot.sendMessage(message).collect {
            println(it)
        }
    }
}