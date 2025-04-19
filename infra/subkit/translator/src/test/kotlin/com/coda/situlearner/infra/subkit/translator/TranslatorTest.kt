package com.coda.situlearner.infra.subkit.translator

import com.coda.situlearner.infra.subkit.translator.en.YouDaoEnglish
import com.coda.situlearner.infra.subkit.translator.ja.TioJapanese
import com.coda.situlearner.infra.subkit.translator.ja.YouDaoJapanese
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TranslatorTest {

    @Test
    fun `test YouDaoEnglish`() = runTest {
        val dictionary = YouDaoEnglish()
        val word = "Barney"
        val infos = dictionary.fetch(word)

        println(infos.toString())
        assertEquals(word, infos[0].word)
    }

    @Test
    fun `test YouDaoJapanese`() = runTest {
        val dictionary = YouDaoJapanese()
        val word = "これ"
        val infos = dictionary.fetch(word)

        println(infos.toString())
        assertEquals(word, infos[0].word)
    }

    @Test
    fun `test YouDaoJapanese no posTag`() = runTest {
        val dictionary = YouDaoJapanese()
        val word = "さつがい"
        val definition = "杀害，杀死"
        val infos = dictionary.fetch(word)

        println(infos.toString())
        assertEquals(definition, infos[0].meanings.firstOrNull()?.definition)
    }

    @Test
    fun `test YouDaoJapanese multiple pronunciations`() = runTest {
        val dictionary = YouDaoJapanese()
        val word = "脅かす"
        val pronunciations = setOf("おどかす⓪③", "おびやかす④")
        val infos = dictionary.fetch(word)

        println(infos.toString())
        assertEquals(pronunciations, infos[0].getPronunciations().toSet())
    }

    @Test
    fun `test TioJapanese`() = runTest {
        // NOTE: the result in web and api does not fully match, e.g. "心配"
        val dictionary = TioJapanese()
        val word = "つる"
        val infos = dictionary.fetch(word)

        println(infos.toString())
        assertEquals(3, infos.size)
        assertEquals("提梁.提梁", infos[0].meanings.first().definition)
    }

    @Test
    fun `test TioJapanese multiple identical words`() = runTest {
        val dictionary = TioJapanese()
        val word = "脅かす"
        val pronunciations = setOf("おどかす", "おびやかす")
        val infos = dictionary.fetch(word)

        println(infos.toString())
        assertEquals(1, infos.size)
        assertEquals(pronunciations, infos[0].getPronunciations().toSet())
    }
}