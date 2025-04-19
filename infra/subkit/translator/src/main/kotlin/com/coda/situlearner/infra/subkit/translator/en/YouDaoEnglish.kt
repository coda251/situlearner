package com.coda.situlearner.infra.subkit.translator.en

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.Translator
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class YouDaoEnglish(
    override val name: String = "YouDao",
    override val sourceLanguage: Language = Language.English,
) : Translator(name, sourceLanguage) {

    override fun fetch(word: String): List<WordInfo> {
        var pronunciation = ""
        val meanings = mutableListOf<WordMeaning>()

        // parse html
        val doc: Document =
            Jsoup.connect("https://dict.youdao.com/result?word=$word&lang=en").get()

        doc.getElementsByClass("phonetic").firstOrNull()?.let {
            pronunciation = it.allElements.text()

        }
        doc.getElementsByClass("word-exp").forEach { element ->
            var posTag = ""
            var paraphrase = ""

            element.children().forEach {
                when (it.className()) {
                    "pos" -> posTag = it.text()
                    "trans" -> paraphrase += it.text()
                }
            }

            meanings += WordMeaning(posTag, paraphrase)
        }

        return listOf(
            WordInfo.fromWebOrUser(
                word = word,
                dictionaryName = name,
                pronunciations = listOf(pronunciation),
                meanings = meanings,
            )
        )
    }
}