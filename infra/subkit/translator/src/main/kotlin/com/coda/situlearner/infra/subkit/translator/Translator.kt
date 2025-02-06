package com.coda.situlearner.infra.subkit.translator

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.en.YouDaoEnglish
import com.coda.situlearner.infra.subkit.translator.ja.YouDaoJapanese

interface Translator {
    val name: String
    val sourceLanguage: Language
    val targetLanguage: Language

    suspend fun query(word: String): WordInfo

    companion object {
        private val registeredTranslators by lazy {
            listOf(
                YouDaoEnglish(),
                YouDaoJapanese()
            )
        }

        fun getTranslators(sourceLanguage: Language, targetLanguage: Language): List<Translator> =
            registeredTranslators.filter {
                it.sourceLanguage == sourceLanguage && it.targetLanguage == targetLanguage
            }
    }
}