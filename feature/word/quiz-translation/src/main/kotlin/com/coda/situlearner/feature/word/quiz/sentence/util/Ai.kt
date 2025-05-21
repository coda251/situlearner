package com.coda.situlearner.feature.word.quiz.sentence.util

import android.content.Context
import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.feature.word.quiz.sentence.QuizState
import com.coda.situlearner.feature.word.quiz.sentence.UiState

internal fun getPrompt(word: Word): String {
    val language = when (word.language) {
        Language.Japanese -> "日语"
        Language.English -> "英语"
        Language.Chinese -> "中文"
        Language.Unknown -> ""
    }
    return """
    你担任命题人提供中文句子，我担任考生完成${language}翻译。随后评估我的翻译质量。你始终使用中文回答。
    你的中文句子中应当包含这1个${language}词汇（${word.word}）的含义，并且这个中文句子对应的${language}句子应当用到常用的${language}句型，且不要包含其它复杂词汇。
    你需要评估我的翻译是否使用了以上提供的词汇并完全还原句子的含义，同时提供地道的翻译结果。
    请你直接返回中文句子，不要提供其它额外信息。
    """.trimIndent()
}

internal fun UiState.ChatSession.asExternalPrompt(): String {
    if (!(quizState == QuizState.LoadingAnswer || quizState == QuizState.Answer)) return ""

    val question = messages[0]
    val answer = messages[1]
    return """
    我正在尝试使用单词${word.word}来翻译句子：${question.content}
    我的翻译是：${answer.content}
    请评价我的翻译结果。
    """.trimIndent()
}

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