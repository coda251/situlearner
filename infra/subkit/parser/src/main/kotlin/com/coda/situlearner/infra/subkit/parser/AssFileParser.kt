package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.buffer

internal class AssFileParser : SubtitleFileParser {

    companion object {
        private const val DIALOG_PREFIX = "Dialogue: "

        private val STYLE_TAG_FORMAT = """\{.*?\}""".toRegex()

        private const val LINE_BREAK_TAG = "\\N"
    }

    override suspend fun parse(filePath: String): List<RawSubtitle> {
        val subtitles = mutableListOf<RawSubtitle>()

        try {
            FileSystem.SYSTEM.source(filePath.toPath()).buffer().use {
                var line: String?
                while (true) {
                    line = it.readUtf8Line() ?: break

                    if (line.startsWith(DIALOG_PREFIX)) {
                        val contentLine = line.substring(DIALOG_PREFIX.length)
                        try {
                            val subtitle = createSubtitle(contentLine)
                            if (subtitle != null) subtitles.add(subtitle)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return subtitles
    }

    private fun createSubtitle(dialogText: String): RawSubtitle? {
        // Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
        val contents = dialogText.split(",")

        val text = contents.subList(9, contents.size).joinToString(",")
            .replace(STYLE_TAG_FORMAT, "")
            .replace(LINE_BREAK_TAG, "\n").trim()

        return if (text.isNotBlank()) RawSubtitle(
            text = text, startTimeInMs = contents[1].toMs(), endTimeInMs = contents[2].toMs()
        ) else null
    }

    private fun String.toMs(): Long {
        // textTime that matches \d{1}:\d{2}:\d{2}.\d{2} exactly
        val hour = substring(0, 1).toInt()
        val minute = substring(2, 4).toInt()
        val second = substring(5, 7).toInt()
        val millisecond = substring(8, 10).toInt()
        val total = millisecond * 10 + second * 1000 + minute * 60 * 1000 + hour * 60 * 60 * 1000
        return total.toLong()
    }
}