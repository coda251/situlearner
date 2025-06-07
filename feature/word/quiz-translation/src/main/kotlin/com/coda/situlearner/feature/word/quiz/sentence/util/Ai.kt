package com.coda.situlearner.feature.word.quiz.sentence.util

import android.content.Context
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.asChinesePrompt

internal fun getReviewPrompt(
    word: Word,
    question: String,
    userAnswer: String
): String = """
    我正在尝试使用${word.language.asChinesePrompt()}单词“${word.word}”来翻译句子：${question}
    我的翻译是：${userAnswer}
    请评价我的翻译结果。
    """.trimIndent()

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