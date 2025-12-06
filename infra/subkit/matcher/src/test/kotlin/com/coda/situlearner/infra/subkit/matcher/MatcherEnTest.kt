package com.coda.situlearner.infra.subkit.matcher

import com.coda.situlearner.infra.subkit.matcher.en.MatcherEn
import org.junit.Assert
import org.junit.Test

class MatcherEnTest {

    private val matcher = MatcherEn()

    @Test
    fun `test lemma`() {
        val q = "simple"
        val t1 = "simplify"
        val t2 = "people"
        val simi1 = matcher.matchLemma(q, t1)
        val simi2 = matcher.matchLemma(q, t2)
        println("q: $q, simi1: $simi1, simi2: $simi2")
        Assert.assertTrue(simi1 >= simi2)
    }

    @Test
    fun `test pronunciation`() {
        val q = "/ kʌt /"
        val t1 = "/ kæt /"
        val t2 = "/ kjuːt /"
        val simi1 = matcher.matchPronunciation(q, t1)
        val simi2 = matcher.matchPronunciation(q, t2)
        println("q: $q, simi1: $simi1, simi2: $simi2")
        Assert.assertTrue(simi1 >= simi2)
    }
}