package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.data.mapper.asExternalModel
import com.coda.situlearner.core.data.mapper.asProto
import com.coda.situlearner.core.datastore.AiStateDataSource
import com.coda.situlearner.core.datastore.AiStateProto
import com.coda.situlearner.core.model.data.AiState
import com.coda.situlearner.core.model.data.ChatbotConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalAiStateRepository(
    private val aiStateDataSource: AiStateDataSource,
): AiStateRepository {
    override val aiState: Flow<AiState>
        get() = aiStateDataSource.aiConfigStoreProto.map(AiStateProto::asExternalModel)

    override suspend fun setAiState(aiState: AiState) {
        aiStateDataSource.setAiStateProto(
            configs = aiState.configs.map(ChatbotConfig::asProto),
            index = aiState.currentIndex
        )
    }
}