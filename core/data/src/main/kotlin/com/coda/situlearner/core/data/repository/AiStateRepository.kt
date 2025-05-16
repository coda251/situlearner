package com.coda.situlearner.core.data.repository

import com.coda.situlearner.core.model.data.AiState
import kotlinx.coroutines.flow.Flow

interface AiStateRepository {

    val aiState: Flow<AiState>

    suspend fun setAiState(aiState: AiState)
}