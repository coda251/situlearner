package com.coda.situlearner.core.model.infra

enum class SubtitleFileFormat(val extension: String) {
    ASS("ass"),
    SRT("srt"),
    LRC("lrc");

    companion object {
        val extensionToFormat = entries.associateBy { it.extension }
    }
}