package com.coda.situlearner.core.model.data

data class AiState(
    /**
     * Registered chatbot configs.
     */
    val configs: ChatbotConfigList,
    val quizPromptTemplate: TranslationQuizPromptTemplate,
    val evalPromptTemplate: TranslationEvalPromptTemplate
)