package com.coda.situlearner.infra.subkit.translator.ja

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.Translator
import com.coda.situlearner.infra.subkit.translator.simplify
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class YouDaoJapanese(
    override val name: String = "YouDao",
    override val sourceLanguage: Language = Language.Japanese,
    override val targetLanguage: Language = Language.Chinese,
) : Translator {

    override suspend fun query(word: String): WordInfo {
        var pronunciation = ""
        val meanings = mutableListOf<WordMeaning>()
        try {
            // parse html
            val doc: Document =
                Jsoup.connect("https://dict.youdao.com/result?word=${word}&lang=ja").get()
            val pronounces = doc.getElementsByClass("head-content")
            val wordExp = doc.getElementsByClass("each-sense")

            if (pronounces.isNotEmpty()) {
                pronunciation = pronounces[0].children().firstOrNull { it.tagName() == "span" }
                    ?.text() ?: ""
            }
            if (wordExp.isNotEmpty()) {
                wordExp.forEach { element ->
                    var posTag = ""
                    var paraphrase = ""

                    element.children().forEach {
                        if (it.className() == "pos-line") {
                            posTag = it.text()
                        } else if (it.className() == "sense-con") {
                            paraphrase += it.text()
                        }
                    }

                    meanings.add(
                        WordMeaning(
                            partOfSpeechTag = posTag,
                            definition = paraphrase
                        )
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return WordInfo(
            word = word,
            dictionaryName = name,
            pronunciation = pronunciation,
            meanings = meanings.simplify(),
        )
    }
}