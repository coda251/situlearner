package com.coda.situlearner.core.model.infra

import kotlinx.serialization.Serializable

@Serializable
data class Subtitle(
    val sourceText: String,
    val targetText: String = "",
    val startTimeInMs: Long,
    val endTimeInMs: Long,
    var tokens: List<Token>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Subtitle) return false
        if (this === other) return true
        return (this.sourceText == other.sourceText &&
                this.targetText == other.targetText &&
                this.startTimeInMs == other.startTimeInMs &&
                this.endTimeInMs == other.endTimeInMs)
    }

    override fun hashCode(): Int {
        var result = sourceText.hashCode()
        result = 31 * result + targetText.hashCode()
        result = 31 * result + startTimeInMs.toInt()
        result = 31 * result + endTimeInMs.toInt()
        return result
    }
}