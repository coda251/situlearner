package com.coda.situlearner.infra.subkit.processor

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.infra.RawSubtitle
import com.coda.situlearner.core.model.infra.Subtitle
import com.coda.situlearner.core.model.infra.SubtitleFileContent
import com.coda.situlearner.core.model.infra.SubtitleFileFormat
import com.coda.situlearner.infra.subkit.lang_detector.LanguageDetector
import com.coda.situlearner.infra.subkit.parser.SubtitleFileParser
import com.coda.situlearner.infra.subkit.tokenizer.Tokenizer
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.math.max
import kotlin.math.min

class ProcessorImpl : Processor {

    override suspend fun load(filePath: String): SubtitleFileContent? {
        return try {
            val file = File(filePath)
            Json.decodeFromString<SubtitleFileContent>(file.readText())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun process(
        filePath: String,
        sourceLanguage: Language,
        targetLanguage: Language,
        tokenizer: Tokenizer,
        detector: LanguageDetector
    ): SubtitleFileContent? {
        val extension = filePath.substringAfterLast(".", missingDelimiterValue = "")
        val format = SubtitleFileFormat.extensionToFormat[extension]

        return format?.let {
            val parser = SubtitleFileParser.getParser(it)
            val subtitles = processSubtitles(
                subtitles = parser.parse(filePath),
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                tokenizer = tokenizer,
                detector = detector,
            )

            SubtitleFileContent(
                subtitles = subtitles,
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
            )
        }
    }
}

private suspend fun processSubtitles(
    subtitles: List<RawSubtitle>,
    sourceLanguage: Language,
    targetLanguage: Language,
    tokenizer: Tokenizer,
    detector: LanguageDetector
): List<Subtitle> = subtitles.map {
    it.toSubtitle(sourceLanguage, targetLanguage, detector)
}.sort().mergeOverlappingSubtitles().filterBlank().setTokens(tokenizer)

private suspend fun RawSubtitle.toSubtitle(
    sourceLanguage: Language,
    targetLanguage: Language,
    detector: LanguageDetector
): Subtitle {
    val languageToText = assignLanguageForTexts(text.split("\n"), detector)
    return Subtitle(
        sourceText = languageToText[sourceLanguage] ?: "",
        targetText = languageToText[targetLanguage] ?: "",
        startTimeInMs = startTimeInMs,
        endTimeInMs = endTimeInMs
    )
}

private suspend fun assignLanguageForTexts(
    texts: List<String>,
    detector: LanguageDetector
): Map<Language, String> = texts.groupBy {
    detector.detect(it)
}.mapValues {
    concatenateTexts(it.value, it.key)
}

private fun concatenateTexts(
    texts: List<String>,
    language: Language
): String {
    return when (language) {
        Language.English -> {
            texts.joinToString("") {
                val t = it.trimEnd()
                if (t.endsWith("-")) t
                else "$t "
            }.trimEnd()
        }

        else -> {
            texts.joinToString("")
        }
    }
}

private fun mergeTwoSubtitleTexts(
    t1: String,
    t2: String,
    separator: String = "- "
): String = if (t1.isNotBlank() && t2.isNotBlank()) {
    listOf(
        separator + t1.removePrefix(separator),
        separator + t2.removePrefix(separator)
    ).joinToString(" ")
} else {
    t1.ifBlank { t2 }
}

private fun mergeTwoSubtitle(
    s1: Subtitle,
    s2: Subtitle
) = Subtitle(
    sourceText = mergeTwoSubtitleTexts(s1.sourceText, s2.sourceText),
    targetText = mergeTwoSubtitleTexts(s1.targetText, s2.targetText),
    startTimeInMs = min(s1.startTimeInMs, s2.startTimeInMs),
    endTimeInMs = max(s1.endTimeInMs, s2.endTimeInMs)
)

private fun hasOverlap(s1: Subtitle, s2: Subtitle) =
    !(s1.endTimeInMs <= s2.startTimeInMs || s2.endTimeInMs <= s1.startTimeInMs)

private fun List<Subtitle>.filterBlank() = filter { it.sourceText.isNotBlank() }

private suspend fun List<Subtitle>.setTokens(tokenizer: Tokenizer) =
    this.onEach {
        it.tokens = tokenizer.tokenize(it.sourceText)
    }

private fun List<Subtitle>.sort() =
    sortedWith(compareBy<Subtitle> { it.startTimeInMs }.thenBy { it.endTimeInMs })

private fun List<Subtitle>.mergeOverlappingSubtitles(): List<Subtitle> {

    val subtitles = mutableListOf<Subtitle>()

    if (this.isEmpty()) return subtitles

    var currentIndex = 0
    var currentSubtitle = this[currentIndex]
    while (true) {
        val nextIndex = currentIndex + 1
        if (nextIndex >= this.size) {
            subtitles.add(currentSubtitle)
            break
        }

        val nextSubtitle = this[nextIndex]
        if (hasOverlap(currentSubtitle, nextSubtitle)) {
            currentSubtitle = mergeTwoSubtitle(currentSubtitle, nextSubtitle)
            currentIndex = nextIndex
        } else {
            subtitles.add(currentSubtitle)
            currentSubtitle = nextSubtitle
            currentIndex = nextIndex
        }
    }

    return subtitles
}