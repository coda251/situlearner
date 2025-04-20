package com.coda.situlearner.infra.subkit.tokenizer

import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.subkit.tokenizer.en.OpenNLP
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class OpenNLPTest {

    private val tokenizer by lazy {
        runBlocking { OpenNLP.build() }
    }

    @Test
    fun `test OpenNLP`() = runTest {
        val text = "Marie was born in Paris."

        val expected = listOf(
            Token(startIndex = 0, endIndex = 5, lemma = "marie"),
            Token(startIndex = 6, endIndex = 9, lemma = "be"),
            Token(startIndex = 10, endIndex = 14, lemma = "bear"),
            Token(startIndex = 15, endIndex = 17, lemma = "in"),
            Token(startIndex = 18, endIndex = 23, lemma = "paris"),
            Token(startIndex = 23, endIndex = 24, lemma = "."),
        )
        val actual = tokenizer.tokenize(text)

        Assert.assertArrayEquals(expected.toTypedArray(), actual.toTypedArray())
    }
}