package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import com.coda.situlearner.core.model.infra.SubtitleFileFormat

interface SubtitleFileParser {

    companion object {
        fun getParser(format: SubtitleFileFormat) = when (format) {
            SubtitleFileFormat.ASS -> AssFileParser()
            SubtitleFileFormat.SRT -> SrtFileParser()
            SubtitleFileFormat.LRC -> LrcFileParser()
        }
    }

    suspend fun parse(filePath: String): List<RawSubtitle>
}