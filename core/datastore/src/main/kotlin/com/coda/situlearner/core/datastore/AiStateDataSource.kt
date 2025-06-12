package com.coda.situlearner.core.datastore

import kotlinx.coroutines.flow.Flow

interface AiStateDataSource {

    val aiStateProto: Flow<AiStateProto>

    suspend fun setChatbotConfigProto(configs: List<ChatbotConfigProto>, index: Int)

    suspend fun setTranslationQuizPromptTemplateProto(template: String)

    suspend fun setTranslationEvalPromptTemplateProto(template: String)
}