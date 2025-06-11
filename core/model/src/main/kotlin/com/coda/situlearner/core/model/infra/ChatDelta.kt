package com.coda.situlearner.core.model.infra

data class ChatDelta(
    val content: String?,
    val isFinished: Boolean = false,
    val error: String? = null,
    val totalTokens: Int? = null
)