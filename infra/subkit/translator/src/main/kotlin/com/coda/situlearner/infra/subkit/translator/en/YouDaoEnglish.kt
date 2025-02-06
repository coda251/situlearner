package com.coda.situlearner.infra.subkit.translator.en

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.Translator
import com.coda.situlearner.infra.subkit.translator.simplify
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class YouDaoEnglish(
    override val name: String = "YouDao",
    override val sourceLanguage: Language = Language.English,
    override val targetLanguage: Language = Language.Chinese,
) : Translator {

    override suspend fun query(word: String): WordInfo {
        var pronunciation = ""
        val meanings = mutableListOf<WordMeaning>()
        try {
            // parse html
            val doc: Document =
                Jsoup.connect("https://dict.youdao.com/result?word=$word&lang=en").get()
            val phonetic = doc.getElementsByClass("phonetic")
            val wordExp = doc.getElementsByClass("word-exp")

            if (phonetic.isNotEmpty()) {
                pronunciation = phonetic[0].allElements.text()
            }
            if (wordExp.isNotEmpty()) {
                wordExp.forEach { element ->
                    var posTag: String? = null
                    var definition = ""

                    element.children().forEach {
                        if (it.className() == "pos") {
                            posTag = it.text()
                        } else if (it.className() == "trans") {
                            definition += it.text()
                        }
                    }

                    posTag?.let {
                        meanings.add(
                            WordMeaning(
                                partOfSpeechTag = it,
                                definition = definition
                            )
                        )
                    }
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