package com.coda.situlearner.infra.subkit.tokenizer

import com.coda.situlearner.core.model.infra.Token
import com.coda.situlearner.infra.subkit.tokenizer.ja.Kuromoji
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class KuromojiTest {

    private val tokenizer by lazy {
        runBlocking { Kuromoji.build() }
    }

    @Test
    fun `test Kuromoji`() = runTest {
        val text = "お寿司が 食べたい。"

        val tokens = tokenizer.tokenize(text)
        val expected = listOf(
            Token(0, 1, "お"),
            Token(1, 3, "寿司"),
            Token(3, 4, "が"),
            Token(5, 7, "食べる"),
            Token(7, 9, "たい"),
            Token(9, 10, "。")
        )
        Assert.assertArrayEquals(expected.toTypedArray(), tokens.toTypedArray())
    }
}