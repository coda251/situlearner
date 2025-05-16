package com.coda.situlearner.core.model.infra

data class ChatMessage(
    val role: ChatRole,
    val content: String
)
