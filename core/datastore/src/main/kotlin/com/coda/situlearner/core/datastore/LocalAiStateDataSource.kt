package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore

internal class LocalAiStateDataSource(private val aiState: DataStore<AiStateProto>) :
    AiStateDataSource {
    override val aiConfigStoreProto = aiState.data

    override suspend fun setAiStateProto(configs: List<ChatbotConfigProto>, index: Int) {
        aiState.updateData {
            it.copy {
                this.configs.clear()
                this.configs.addAll(configs)
                this.currentIndex = index
            }
        }
    }
}