package com.coda.situlearner.infra.subkit.translator

import com.coda.situlearner.infra.subkit.translator.en.YouDaoEnglish
import com.coda.situlearner.infra.subkit.translator.ja.YouDaoJapanese
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TranslatorTest {

    @Test
    fun `test YouDaoEnglish`() = runTest {
        val dictionary = YouDaoEnglish()
        val word = "Barney"
        val situWord = dictionary.query(word)

        println(situWord.toString())
        assertEquals(word, situWord.word)
    }

    @Test
    fun `test YouDaoJapanese`() = runTest {
        val dictionary = YouDaoJapanese()
        val word = "これ"
        val situWord = dictionary.query(word)

        println(situWord.toString())
        assertEquals(word, situWord.word)
    }

    @Test
    fun `test YouDaoJapanese no posTag`() = runTest {
        val dictionary = YouDaoJapanese()
        val word = "さつがい"
        val definition = "杀害，杀死"
        val situWord = dictionary.query(word)

        println(situWord.toString())
        assertEquals(definition, situWord.meanings?.firstOrNull()?.definition)
    }
}