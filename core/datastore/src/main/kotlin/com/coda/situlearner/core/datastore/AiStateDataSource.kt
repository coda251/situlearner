package com.coda.situlearner.core.datastore

import kotlinx.coroutines.flow.Flow

interface AiStateDataSource {

    val aiConfigStoreProto: Flow<AiStateProto>

    suspend fun setAiStateProto(configs: List<ChatbotConfigProto>, index: Int)
}