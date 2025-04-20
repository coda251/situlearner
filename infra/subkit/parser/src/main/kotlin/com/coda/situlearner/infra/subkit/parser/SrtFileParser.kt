package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.buffer

internal class SrtFileParser : SubtitleFileParser {

    companion object {
        private val TIMESTAMP_FORMAT =
            """\d{2}:\d{2}:\d{2},\d{3} --> \d{2}:\d{2}:\d{2},\d{3}""".toRegex()

        private val HTML_TAG_FORMAT =
            """<.*?>""".toRegex()
    }

    override suspend fun parse(filePath: String): List<RawSubtitle> {
        val subtitles = mutableListOf<RawSubtitle>()

        try {
            FileSystem.SYSTEM.source(filePath.toPath()).buffer().use { source ->
                var line: String?
                while (true) {
                    line = source.readUtf8Line() ?: break

                    // a subtitle block
                    TIMESTAMP_FORMAT.find(line)?.let {

                        // time part
                        val timeStampText = it.groupValues[0]

                        // content part
                        val contentLines = buildList {
                            while (true) {
                                val contentLine = source.readUtf8Line()
                                // a blank text indicates this the end of this block
                                if (contentLine.isNullOrBlank()) break
                                else add(contentLine)
                            }
                        }

                        try {
                            val subtitle = createSubtitle(timeStampText, contentLines)
                            if (subtitle != null) subtitles.add(subtitle)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } ?: continue
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return subtitles
    }

    private fun createSubtitle(
        timeStampText: String,
        contentTexts: List<String>,
    ): RawSubtitle? {
        val sourceText = contentTexts.joinToString("\n") {
            it.replace(HTML_TAG_FORMAT, "")
        }.trim()

        return if (sourceText.isNotBlank()) {
            RawSubtitle(
                text = sourceText,
                startTimeInMs = timeStampText.substring(0, 12).toMs(),
                endTimeInMs = timeStampText.substring(17, 29).toMs(),
            )
        } else {
            null
        }
    }

    private fun String.toMs(): Long {
        // textTime that matches \d{2}:\d{2}:\d{2},\d{3} exactly
        val hour = substring(0, 2).toInt()
        val minute = substring(3, 5).toInt()
        val second = substring(6, 8).toInt()
        val millisecond = substring(9, 12).toInt()
        val total = millisecond + second * 1000 + minute * 60 * 1000 + hour * 60 * 60 * 1000
        return total.toLong()
    }
}