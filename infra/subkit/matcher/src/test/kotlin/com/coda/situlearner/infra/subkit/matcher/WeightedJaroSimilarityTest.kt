package com.coda.situlearner.infra.subkit.matcher

import org.junit.Assert
import org.junit.Test

class WeightedJaroSimilarityTest {

    @Test
    fun `test no weights with prefix similarity`() {
        val q = "abc"
        val t1 = "acbc"
        val t2 = "adbc"
        val sim1 = weightedJaroWinklerSimilarity(q, t1)
        val sim2 = weightedJaroWinklerSimilarity(q, t2)
        val expected1 = 0.825
        val expected2 = 0.9249
        // in fact, we expect that sim1 would be no less than sim2 intuitively,
        // however, the greed match mode in jaro mismatches the 2th 'c' in t1 with
        // the 3rd 'c' in q, thus causing additional transposition penalty.
        // may use bidirectional as a heuristics method to reduce such cases
        Assert.assertEquals(sim1, expected1, 0.0001)
        Assert.assertEquals(sim2, expected2, 0.0001)
    }

    @Test
    fun `test weights`() {
        val s1 = "aed"
        val s2 = "adob"
        val vowels = setOf('a', 'e', 'i', 'o', 'u')
        val sim = weightedJaroSimilarity(
            s1, s2,
            charSim = { c1, c2 ->
                if (c1 == c2) 1.0
                else if ((c1 in vowels && c2 in vowels) || (c1 !in vowels && c2 !in vowels)) 0.5
                else 0.0
            },
            charBaseWeight = { if (it in vowels) 0.8 else 1.0 }
        )
        val expected = 0.7130
        Assert.assertEquals(expected, sim, 0.0001)
    }
}