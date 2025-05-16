package com.coda.situlearner.core.model.infra

sealed interface ChatResponse {
    data class Success(val content: String) : ChatResponse
    data class Error(val code: Int? = null, val message: String) : ChatResponse
}