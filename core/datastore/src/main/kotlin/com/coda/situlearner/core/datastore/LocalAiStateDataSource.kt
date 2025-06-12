package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore

internal class LocalAiStateDataSource(private val aiState: DataStore<AiStateProto>) :
    AiStateDataSource {
    override val aiStateProto = aiState.data

    override suspend fun setChatbotConfigProto(configs: List<ChatbotConfigProto>, index: Int) {
        aiState.updateData {
            it.copy {
                this.configs.clear()
                this.configs.addAll(configs)
                this.currentIndex = index
            }
        }
    }

    override suspend fun setTranslationQuizPromptTemplateProto(template: String) {
        aiState.updateData {
            it.copy {
                this.translationQuizPromptTemplate = template
            }
        }
    }

    override suspend fun setTranslationEvalPromptTemplateProto(template: String) {
        aiState.updateData {
            it.copy {
                this.translationEvalPromptTemplate = template
            }
        }
    }
}