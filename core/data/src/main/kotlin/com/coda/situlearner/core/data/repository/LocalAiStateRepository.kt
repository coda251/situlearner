package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asProto
import com.coda.situlearner.core.datastore.AiStateDataSource
import com.coda.situlearner.core.datastore.AiStateProto
import com.coda.situlearner.core.model.data.AiState
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.ChatbotConfigList
import com.coda.situlearner.core.model.data.TranslationEvalPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalAiStateRepository(
    private val aiStateDataSource: AiStateDataSource,
) : AiStateRepository {
    override val aiState: Flow<AiState>
        get() = aiStateDataSource.aiStateProto.map(AiStateProto::asExternalModel)

    override suspend fun setChatbotConfigList(chatbotConfigList: ChatbotConfigList) {
        aiStateDataSource.setChatbotConfigProto(
            configs = chatbotConfigList.configs.map(ChatbotConfig::asProto),
            index = chatbotConfigList.currentIndex
        )
    }

    override suspend fun setTranslationQuizPromptTemplate(template: TranslationQuizPromptTemplate) {
        aiStateDataSource.setTranslationQuizPromptTemplateProto(template.data)
    }

    override suspend fun setTranslationEvalPromptTemplate(template: TranslationEvalPromptTemplate) {
        aiStateDataSource.setTranslationEvalPromptTemplateProto(template.data)
    }
}