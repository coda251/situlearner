package com.coda.situlearner.core.model.infra

data class RawSubtitle(
    val startTimeInMs: Long,
    val endTimeInMs: Long,
    val text: String,
) {
    init {
        require(startTimeInMs < endTimeInMs) {
            "Invalid time interval: startTimeInMs=$startTimeInMs, endTimeInMs=$endTimeInMs"
        }
    }
}