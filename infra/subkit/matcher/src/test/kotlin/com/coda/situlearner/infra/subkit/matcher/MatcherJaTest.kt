package com.coda.situlearner.infra.subkit.matcher

import com.coda.situlearner.infra.subkit.matcher.ja.MatcherJa
import org.junit.Assert
import org.junit.Test

class MatcherJaTest {

    private val matcher = MatcherJa()

    @Test
    fun `test lemma`() {
        val q = "他人行儀"
        val t1 = "他"
        val t2 = "よろしい"
        val simi1 = matcher.matchLemma(q, t1)
        val simi2 = matcher.matchLemma(q, t2)
        println("q: $q, simi1: $simi1, simi2: $simi2")
        Assert.assertTrue(simi1 >= simi2)
    }

    @Test
    fun `test lemma with kanji`() {
        val q = "離れる"
        val t1 = "離す"
        val t2 = "撮れる"
        val simi1 = matcher.matchLemma(q, t1)
        val simi2 = matcher.matchLemma(q, t2)
        println("q: $q, simi1: $simi1, simi2: $simi2")
        Assert.assertTrue(simi1 >= simi2)
    }

    @Test
    fun `test pronunciation`() {
        val q = "おしい"
        val t1 = "おいしい"
        val t2 = "おかしい"
        val simi1 = matcher.matchPronunciation(q, t1)
        val simi2 = matcher.matchPronunciation(q, t2)
        println("q: $q, simi1: $simi1, simi2: $simi2")
        Assert.assertTrue(simi1 >= simi2)
    }
}