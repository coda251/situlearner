package com.coda.situlearner.infra.subkit.tokenizer.ja

import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.subkit.tokenizer.Tokenizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.atilika.kuromoji.ipadic.Tokenizer as JaTokenizer

internal class Kuromoji private constructor(
    private val tokenizer: JaTokenizer
) : Tokenizer {

    companion object {
        suspend fun build() =
            withContext(Dispatchers.IO) { Kuromoji(JaTokenizer.Builder().build()) }
    }

    override suspend fun tokenize(text: String): List<Token> {
        val tokens = tokenizer.tokenize(text)

        return buildList {
            tokens.onEach {
                // some nonsense token, such as space, will be removed
                if (it.isKnown) {
                    add(
                        Token(
                            startIndex = it.position,
                            endIndex = it.position + it.surface.length,
                            lemma = it.baseForm
                        )
                    )
                }
            }
        }
    }
}