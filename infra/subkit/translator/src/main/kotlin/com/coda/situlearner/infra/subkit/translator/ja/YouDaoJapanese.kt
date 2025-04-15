package com.coda.situlearner.infra.subkit.translator.ja

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.Translator
import com.coda.situlearner.infra.subkit.translator.simplify
import org.jsoup.Jsoup

class YouDaoJapanese(
    override val name: String = "YouDao",
    override val sourceLanguage: Language = Language.Japanese,
) : Translator(name, sourceLanguage) {

    override fun fetch(word: String): List<WordInfo> {
        val pronunciations = mutableListOf<String>()
        val meanings = mutableListOf<WordMeaning>()

        // parse html
        // NOTE： is there a way to get word options from YouDao?
        val doc = Jsoup.connect("https://dict.youdao.com/result?word=${word}&lang=ja").get()
        doc.getElementsByClass("head-content").forEach { element ->
            element.children()
                .firstOrNull { it.tagName() == "span" }
                ?.text()
                ?.takeIf { it.isNotEmpty() }
                ?.let(pronunciations::add)
        }

        doc.getElementsByClass("each-sense").forEach { element ->
            var posTag = ""
            var paraphrase = ""

            element.children().forEach {
                when (it.className()) {
                    "pos-line" -> posTag = it.text()
                    "sense-con" -> paraphrase += it.text()
                }
            }
            meanings += WordMeaning(posTag, paraphrase)
        }

        return listOf(
            WordInfo(
                word = word,
                dictionaryName = name,
                pronunciations = pronunciations,
                meanings = meanings.simplify(),
            )
        )
    }
}