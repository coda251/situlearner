package com.coda.situlearner.infra.subkit.tokenizer.en

import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.subkit.tokenizer.Tokenizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import opennlp.tools.lemmatizer.LemmatizerME
import opennlp.tools.lemmatizer.LemmatizerModel
import opennlp.tools.postag.POSModel
import opennlp.tools.postag.POSTaggerME
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel

internal class OpenNLP private constructor(
    private val tagger: POSTaggerME,
    private val tokenizer: TokenizerME,
    private val lemmatizer: LemmatizerME,
) : Tokenizer {

    companion object {
        suspend fun build(): OpenNLP = withContext(Dispatchers.IO) {
            OpenNLP(
                POSTaggerME(POSModel(getModelUrl("opennlp-en-ud-ewt-pos-1.2-2.5.0.bin"))),
                TokenizerME(TokenizerModel(getModelUrl("opennlp-en-ud-ewt-tokens-1.2-2.5.0.bin"))),
                LemmatizerME(LemmatizerModel(getModelUrl("opennlp-en-ud-ewt-lemmas-1.2-2.5.0.bin")))
            )
        }

        private fun getModelUrl(fileName: String) =
            OpenNLP::class.java.classLoader!!.getResource(fileName)
    }

    override suspend fun tokenize(text: String): List<Token> {
        val spans = tokenizer.tokenizePos(text)
        val tokens = spans.map { text.substring(it.start, it.end) }.toTypedArray()
        val tags = tagger.tag(tokens)
        val lemmas = lemmatizer.lemmatize(tokens, tags)

        return buildList {
            spans.forEachIndexed { index, it ->
                add(
                    Token(
                        startIndex = it.start,
                        endIndex = it.end,
                        lemma = lemmas[index]
                    )
                )
            }
        }
    }
}