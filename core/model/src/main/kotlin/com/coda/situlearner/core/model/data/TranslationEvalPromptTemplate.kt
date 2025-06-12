package com.coda.situlearner.core.model.data

@JvmInline
value class TranslationEvalPromptTemplate(val data: String) {

    companion object {
        val DEFAULT = TranslationEvalPromptTemplate(PROMPT_TEMPLATE)
    }

    fun buildPrompt(word: Word, question: String, answer: String): String =
        data.replace(LANGUAGE_TAG, word.language.asChinesePrompt())
            .replace(WORD_TAG, word.word)
            .replace(QUESTION_TAG, question)
            .replace(ANSWER_TAG, answer)
}

private const val LANGUAGE_TAG = "{语言}"
private const val WORD_TAG = "{单词}"
private const val QUESTION_TAG = "{问题}"
private const val ANSWER_TAG = "{回答}"
private const val PROMPT_TEMPLATE =
    """我正在尝试使用${LANGUAGE_TAG}单词“${WORD_TAG}”来翻译句子：${QUESTION_TAG}
我的翻译是：${ANSWER_TAG}
请评价我的翻译结果。"""