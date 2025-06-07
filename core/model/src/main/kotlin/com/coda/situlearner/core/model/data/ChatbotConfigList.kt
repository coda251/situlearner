package com.coda.situlearner.core.model.data

data class ChatbotConfigList(
    /**
     * Each config should contain one unique [ChatbotType].
     */
    val configs: List<ChatbotConfig>,
    val currentIndex: Int,
) : List<ChatbotConfig> by configs {

    val currentItem: ChatbotConfig?
        get() = getOrNull(currentIndex)
}