package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.buffer

internal class LrcFileParser : SubtitleFileParser {

    companion object {
        private val TIMESTAMP_FORMAT = """\[\d{2}:\d{2}\.\d{2}]""".toRegex()
    }

    override suspend fun parse(filePath: String): List<RawSubtitle> {
        val subtitles = mutableListOf<RawSubtitle>()

        try {
            FileSystem.SYSTEM.source(filePath.toPath()).buffer().use { source ->
                var line: String?
                while (true) {
                    line = source.readUtf8Line() ?: break

                    TIMESTAMP_FORMAT.find(line)?.let {
                        val timeStampText = it.groupValues[0]
                        val content = line.substring(10)
                        if (content.isNotBlank()) {
                            try {
                                subtitles.add(createSubtitle(timeStampText, content))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } ?: continue
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return subtitles.resolveEndTime()
    }

    private fun createSubtitle(timeStampText: String, content: String) = RawSubtitle(
        text = content.trim(),
        startTimeInMs = timeStampText.substring(1, 9).toMs(),
        endTimeInMs = -1L,
    )

    private fun String.toMs(): Long {
        // textTime that matches \d{2}:\d{2}.\d{2} exactly
        val minute = substring(0, 2).toInt()
        val second = substring(3, 5).toInt()
        val millisecond = substring(6, 8).toInt()
        val total = millisecond * 10 + second * 1000 + minute * 60 * 1000
        return total.toLong()
    }

    private fun List<RawSubtitle>.resolveEndTime(): List<RawSubtitle> {
        if (isEmpty()) return this

        val groupedSubtitles = groupBy { it.startTimeInMs }
        val sortedStartTimes = groupedSubtitles.keys.sorted()

        return buildList {
            for (i in sortedStartTimes.indices) {
                val currentStartTime = sortedStartTimes[i]
                val currentGroup = groupedSubtitles[currentStartTime]!!

                val nextStartTime = if (i < sortedStartTimes.size - 1) {
                    sortedStartTimes[i + 1]
                } else {
                    Long.MAX_VALUE
                }

                currentGroup.forEach { add(it.copy(endTimeInMs = nextStartTime)) }
            }
        }
    }
}