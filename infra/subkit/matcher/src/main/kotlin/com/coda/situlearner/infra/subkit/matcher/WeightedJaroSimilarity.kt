package com.coda.situlearner.infra.subkit.matcher

private const val JW_SCALING_FACTOR = 0.1
private const val JW_MAX_PREFIX_LENGTH = 4

internal fun weightedJaroWinklerSimilarity(
    s1: String,
    s2: String,
    charSim: (Char, Char) -> Double = { c1, c2 -> if (c1 == c2) 1.0 else 0.0 },
    charBaseWeight: (Char) -> Double = { 1.0 }
): Double {
    val sj = weightedJaroSimilarity(s1, s2, charSim, charBaseWeight)
    val commonPrefix = s1.commonPrefixWith(s2)
    return sj + JW_SCALING_FACTOR * minOf(commonPrefix.length, JW_MAX_PREFIX_LENGTH) * (1.0 - sj)
}

internal fun weightedJaroSimilarity(
    s1: String,
    s2: String,
    charSim: (Char, Char) -> Double = { c1, c2 -> if (c1 == c2) 1.0 else 0.0 },
    charBaseWeight: (Char) -> Double = { 1.0 }
): Double {
    if (s1 == s2) return 1.0
    val totalWeight1 = s1.sumOf { charBaseWeight(it) }
    val totalWeight2 = s2.sumOf { charBaseWeight(it) }
    if (totalWeight1 == 0.0 || totalWeight2 == 0.0) return 0.0

    val (m, t) = match(s1, s2, charSim, charBaseWeight)
    if (m == 0.0) return 0.0
    val j = (m / totalWeight1 + m / totalWeight2 + (m - t) / m) / 3
    return j
}

private fun match(
    s1: String,
    s2: String,
    charSim: (Char, Char) -> Double,
    charBaseWeight: (Char) -> Double
): Pair<Double, Double> {
    val len1 = s1.length
    val len2 = s2.length
    val matchedDist = maxOf(0, maxOf(len1, len2) / 2 - 1)

    val matchedWeights = DoubleArray(len1)
    val matchIndexes1 = IntArray(len1) { -1 }
    val matchIndexes2 = IntArray(len2) { -1 }

    // 1. match
    for (i in 0 until len1) {
        val start = maxOf(0, i - matchedDist)
        val end = minOf(len2, i + matchedDist + 1)

        var bestJ = -1
        var bestSim = 0.0

        for (j in start until end) {
            if (matchIndexes2[j] != -1) continue
            val sim = charSim(s1[i], s2[j])
            if (sim > bestSim) {
                bestJ = j
                bestSim = sim
            }
        }

        if (bestJ != -1 && bestSim > 0.0) {
            matchIndexes1[i] = bestJ
            matchIndexes2[bestJ] = i
            val weight = charBaseWeight(s1[i]) * bestSim
            matchedWeights[i] = weight
        }
    }

    val matchedWeight = matchedWeights.sum()
    if (matchedWeight == 0.0) return 0.0 to 0.0

    // 2. transposition penalty
    var transpositionWeight = 0.0
    var j = 0
    for (i in 0 until len1) {
        if (matchIndexes1[i] == -1) continue
        while (matchIndexes2[j] == -1) j++
        if (matchIndexes1[i] != j) transpositionWeight += matchedWeights[i]
        j++
    }
    transpositionWeight /= 2

    return matchedWeight to transpositionWeight
}