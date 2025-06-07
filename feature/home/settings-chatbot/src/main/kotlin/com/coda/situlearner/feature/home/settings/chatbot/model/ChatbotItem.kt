package com.coda.situlearner.feature.home.settings.chatbot.model

import androidx.annotation.DrawableRes
import com.coda.situlearner.core.model.data.Aliyun
import com.coda.situlearner.core.model.data.ChatbotConfig
import com.coda.situlearner.core.model.data.ChatbotConfigList
import com.coda.situlearner.core.model.data.ChatbotType
import com.coda.situlearner.feature.home.settings.chatbot.R

internal data class ChatbotItem(
    val type: ChatbotType,
    @DrawableRes val icon: Int,
    val modelName: String? = null,
    val cfg: ChatbotConfig? = null,
    val status: Status = Status.Unregistered,
) {
    enum class Status(val level: Int) {
        Active(0),
        Registered(1),
        Unregistered(2),
    }
}

internal fun ChatbotType.asChatbotItem(): ChatbotItem {
    return when (this) {
        ChatbotType.Aliyun -> {
            ChatbotItem(
                type = this,
                icon = R.drawable.aliyun_bailian,
            )
        }
    }
}

internal fun ChatbotConfig.asChatbotItem(isSelect: Boolean): ChatbotItem {
    return when (this) {
        is Aliyun -> {
            this.type.asChatbotItem().copy(
                modelName = this.model,
                cfg = this,
                status = if (isSelect) ChatbotItem.Status.Active else ChatbotItem.Status.Registered
            )
        }
    }
}

internal fun List<ChatbotItem>.asAiState(updatedConfig: ChatbotConfig): ChatbotConfigList {
    // update configs
    val configs = mapNotNull { it.cfg }.toMutableList().apply {
        val index = this.indexOfFirst { updatedConfig.type == it.type }
        if (index != -1) this[index] = updatedConfig
        else this.add(updatedConfig)
    }

    // since the configs is mutated only by addition, so the currentIndex can be directly used
    val currentIndex = this.indexOfFirst { it.status == ChatbotItem.Status.Active }

    return ChatbotConfigList(
        configs = configs,
        currentIndex = currentIndex.coerceAtLeast(0)
    )
}

internal fun List<ChatbotItem>.asAiState(currentType: ChatbotType): ChatbotConfigList {
    val configs = mapNotNull { it.cfg }.toMutableList()
    val currentIndex = configs.indexOfFirst { it.type == currentType }
    return ChatbotConfigList(
        configs = configs,
        currentIndex = currentIndex
    )
}