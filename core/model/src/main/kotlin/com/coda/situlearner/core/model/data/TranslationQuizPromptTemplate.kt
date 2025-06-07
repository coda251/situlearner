package com.coda.situlearner.core.model.data

@JvmInline
value class TranslationQuizPromptTemplate(
    val data: String,
) {
    companion object {
        val DEFAULT = TranslationQuizPromptTemplate(
            PROMPT_TEMPLATE
        )
    }

    fun buildPrompt(word: Word): String =
        data.replace(LANGUAGE_TAG, word.language.asChinesePrompt()).replace(WORD_TAG, word.word)
}

private const val LANGUAGE_TAG = "{语言}"
private const val WORD_TAG = "{单词}"
private const val PROMPT_TEMPLATE =
    "使用${LANGUAGE_TAG}词汇“${WORD_TAG}”造一个句子，句子应当用到常用的${LANGUAGE_TAG}句型，且不要包含其它复杂词汇。请不要回答${LANGUAGE_TAG}句子，只返回它的中文翻译。"

fun Language.asChinesePrompt() = when (this) {
    Language.Japanese -> "日语"
    Language.English -> "英语"
    Language.Chinese -> "汉语"
    Language.Unknown -> ""
}