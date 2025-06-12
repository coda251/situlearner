package com.coda.situlearner.core.data.mapper

import com.coda.situlearner.core.datastore.AiStateProto
import com.coda.situlearner.core.datastore.AliyunConfigProto
import com.coda.situlearner.core.datastore.ChatbotConfigProto
import com.coda.situlearner.core.model.data.AiState
import com.coda.situlearner.core.model.data.Aliyun
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.ChatbotConfigList
import com.coda.situlearner.core.model.data.TranslationEvalPromptTemplate
import com.coda.situlearner.core.model.data.TranslationQuizPromptTemplate

internal fun ChatbotConfigProto.asExternalModel(): ChatbotConfig? = when (detailsCase) {
    ChatbotConfigProto.DetailsCase.ALIYUN -> Aliyun(
        apiKey = aliyun.apiKey,
        model = aliyun.model,
    )

    else -> null
}

internal fun ChatbotConfig.asProto() = ChatbotConfigProto.newBuilder().apply {
    when (this@asProto) {
        is Aliyun -> {
            this.setAliyun(
                AliyunConfigProto.newBuilder().apply {
                    this.apiKey = this@asProto.apiKey
                    this.model = this@asProto.model
                }
            )
        }
    }
}.build()

internal fun AiStateProto.asExternalModel() = AiState(
    configs = ChatbotConfigList(
        configs = configsList.mapNotNull(ChatbotConfigProto::asExternalModel),
        currentIndex = currentIndex
    ),
    quizPromptTemplate = if (translationQuizPromptTemplate.isNotEmpty()) TranslationQuizPromptTemplate(
        translationQuizPromptTemplate
    ) else TranslationQuizPromptTemplate.DEFAULT,
    evalPromptTemplate = if (translationEvalPromptTemplate.isNotEmpty()) TranslationEvalPromptTemplate(
        translationEvalPromptTemplate
    ) else TranslationEvalPromptTemplate.DEFAULT
)