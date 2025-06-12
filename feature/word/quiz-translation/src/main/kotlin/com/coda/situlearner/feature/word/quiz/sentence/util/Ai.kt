package com.coda.situlearner.feature.word.quiz.sentence.util

import android.content.Context

internal enum class ExternalChatbot(
    val packageName: String,
    val displayName: String
) {
    ChatGPT("com.openai.chatgpt", "ChatGPT"),
}

internal fun launchExternalChatbot(
    context: Context,
    externalChatbot: ExternalChatbot
) {
    val intent = context.packageManager.getLaunchIntentForPackage(externalChatbot.packageName)
    if (intent != null) {
        context.startActivity(intent)
    }
}