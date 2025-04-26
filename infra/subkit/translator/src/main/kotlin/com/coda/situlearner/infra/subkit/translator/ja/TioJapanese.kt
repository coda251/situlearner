package com.coda.situlearner.infra.subkit.translator.ja

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.Translator
import org.jsoup.Jsoup

internal class TioJapanese(
    override val name: String = "Tio",
    override val sourceLanguage: Language = Language.Japanese,
) : Translator(name, sourceLanguage) {

    override suspend fun fetch(word: String): List<WordInfo> {
        val infos = mutableListOf<WordInfo>()
        val pattern = Regex("""^(\S+)\s+【([^】]+)】\s*(.*)$""")
        val meaningPattern = Regex("""((?:\[[^]]+])+)\s*(.*?)(?=\s*\[|$)""")

        // parse html
        val doc = Jsoup.connect("https://tio.freemdict.com/japi?key=${word}").get()
        doc.getElementsByClass("jp_exam").forEach { element ->
            pattern.matchEntire(element.text().trim())?.let { matchResult ->
                val (wordWeb, pronunciation, meaning) = matchResult.destructured
                if (wordWeb == word || pronunciation == word) {
                    infos += WordInfo.fromWebOrUser(
                        word = wordWeb,
                        dictionaryName = name,
                        pronunciations = listOf(pronunciation),
                        meanings = meaningPattern.findAll(meaning).map {
                            val pos = it.groupValues[1]
                            val definition = it.groupValues[2].trim()
                            WordMeaning(pos, definition)
                        }.toList().ifEmpty { listOf(WordMeaning("", meaning.trim())) },
                    )
                }
            }
        }

        return infos
    }
}