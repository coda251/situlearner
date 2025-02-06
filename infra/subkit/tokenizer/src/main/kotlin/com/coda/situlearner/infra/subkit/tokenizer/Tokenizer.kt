package com.coda.situlearner.infra.subkit.tokenizer

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.subkit.tokenizer.en.OpenNLP
import com.coda.situlearner.infra.subkit.tokenizer.ja.Kuromoji

interface Tokenizer {

    companion object {
        fun getTokenizer(language: Language): Tokenizer? = when (language) {
            Language.English -> OpenNLP()
            Language.Japanese -> Kuromoji()
            else -> null
        }
    }

    suspend fun tokenize(text: String): List<Token>
}