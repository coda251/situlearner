package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.AiState
import com.coda.situlearner.core.model.data.ChatbotConfigList
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate
import kotlinx.coroutines.flow.Flow

interface AiStateRepository {

    val aiState: Flow<AiState>

    suspend fun setChatbotConfigList(chatbotConfigList: ChatbotConfigList)

    suspend fun setTranslationQuizPromptTemplate(template: TranslationQuizPromptTemplate)
}