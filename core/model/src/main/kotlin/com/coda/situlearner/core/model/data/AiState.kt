package com.coda.situlearner.core.model.data

data class AiState(
    /**
     * Each config should contain one unique [ChatbotType].
     */
    val configs: List<ChatbotConfig>,
    val currentIndex: Int,
)