package com.coda.situlearner.infra.subkit.matcher

internal fun weightedLevenshteinSimilarity(
    query: String,
    target: String,
    deletion: (Char) -> Double = { 1.0 },
    insertion: (Char) -> Double = { 1.0 },
    substitution: (Char, Char) -> Double = { c1, c2 -> if (c1 == c2) 0.0 else 1.0 }
): Double {
    // empty string for max distance
    val maxDistance = maxOf(query.sumOf { deletion(it) }, target.sumOf { insertion(it) })
    if (maxDistance == 0.0) return 1.0
    val distance = weightedLevenshteinDistance(query, target, deletion, insertion, substitution)
    return (1.0 - distance / maxDistance).coerceIn(0.0, 1.0)
}

private fun weightedLevenshteinDistance(
    s1: String,
    s2: String,
    deletion: (Char) -> Double,
    insertion: (Char) -> Double,
    substitution: (Char, Char) -> Double,
): Double {
    val (a, b) = if (s1.length >= s2.length) s1 to s2 else s2 to s1
    val m = a.length
    val n = b.length

    var prev = DoubleArray(n + 1)
    var curr = DoubleArray(n + 1)
    for (j in 1..n) prev[j] = prev[j - 1] + deletion(b[j - 1])

    for (i in 1..m) {
        curr[0] = prev[0] + deletion(a[i - 1])

        for (j in 1..n) {
            val delete = prev[j] + deletion(a[i - 1])
            val insert = curr[j - 1] + insertion(b[j - 1])
            val sub = prev[j - 1] + substitution(a[i - 1], b[j - 1])
            curr[j] = minOf(delete, insert, sub)
        }

        val tmp = prev
        prev = curr
        curr = tmp
    }
    return prev[n]
}