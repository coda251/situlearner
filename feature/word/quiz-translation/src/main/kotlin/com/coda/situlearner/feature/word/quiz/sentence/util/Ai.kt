package com.coda.situlearner.feature.word.quiz.sentence.util

import android.content.ClipData
import android.content.Context
import androidx.compose.ui.platform.Clipboard

internal fun launchExternalChatbotWithClip(
    data: String,
    clipboard: Clipboard,
    context: Context,
    chatbot: ExternalChatbot = ExternalChatbot.ChatGPT
) {
    clipboard.nativeClipboard.setPrimaryClip(ClipData.newPlainText("text", data))
    launchExternalChatbot(context, chatbot)
}

internal enum class ExternalChatbot(
    val packageName: String,
    val displayName: String
) {
    ChatGPT("com.openai.chatgpt", "ChatGPT"),
}

private fun launchExternalChatbot(
    context: Context,
    externalChatbot: ExternalChatbot
) {
    val intent = context.packageManager.getLaunchIntentForPackage(externalChatbot.packageName)
    if (intent != null) {
        context.startActivity(intent)
    }
}