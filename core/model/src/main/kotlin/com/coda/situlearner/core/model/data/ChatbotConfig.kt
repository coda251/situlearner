package com.coda.situlearner.core.model.data

sealed class ChatbotConfig(open val type: ChatbotType)

data class Aliyun(
    override val type: ChatbotType = ChatbotType.Aliyun,
    val apiKey: String,
    val model: String,
): ChatbotConfig(type) {
    companion object {
        val Default = Aliyun(
            apiKey = "",
            model = "deepseek-v3"
        )
    }
}