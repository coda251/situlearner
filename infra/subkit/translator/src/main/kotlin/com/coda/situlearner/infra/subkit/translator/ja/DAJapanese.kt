package com.coda.situlearner.infra.subkit.translator.ja

import com.coda.situlearner.core.model.data.Language
import com.coda.situlearner.core.model.data.WordMeaning
import com.coda.situlearner.core.model.infra.WordInfo
import com.coda.situlearner.infra.subkit.translator.Translator
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class DAJapanese(
    override val name: String = "DA",
    override val sourceLanguage: Language = Language.Japanese,
) : Translator(name, sourceLanguage) {

    // NOTE: this is a workaround for jsoup >= 1.21.1 which use
    // HttpClient (do not accept sslSocketFactory) instead of HttpURLConnection,
    // so we need to create a customized client for html query.
    private val client by lazy {
        this.javaClass.getResourceAsStream("/dict.asia.crt")?.let {
            createClientWithCertificate(it)
        } ?: HttpClient(CIO)
    }

    override suspend fun fetch(word: String): List<WordInfo> {
        val url = "https://dict.asia/jc/$word"
        val html = client.get(url).bodyAsText()
        val doc = Jsoup.parse(html, url)

        val infos = doc.select("div#jp_comment").map { block ->
            val wordFromWeb = block.selectFirst("span.jpword")
                ?.text()
                ?.trim()
                .orEmpty()
            val kana = block.selectFirst("span[title=假名]")
                ?.text()
                ?.replace("【", "")
                ?.replace("】", "")
                ?.trim()
                .orEmpty()
            val tone = block.selectFirst("span[title=音调]")
                ?.text()
                ?.trim()
                .orEmpty()
            val pronunciation = kana + tone

            val span = block.select("div.jp_explain span.commentItem").firstOrNull()
            val meanings = span?.let { parseWordMeanings(it) } ?: emptyList()

            WordInfo.fromWebOrUser(
                word = wordFromWeb,
                dictionaryName = name,
                pronunciations = listOf(pronunciation),
                meanings = meanings
            )
        }
        return infos
    }

    private fun parseWordMeanings(span: Element): List<WordMeaning> {
        val meanings = mutableListOf<WordMeaning>()
        var currentPos: String? = null
        val defBuf = StringBuilder()

        fun flush() {
            if (currentPos != null && defBuf.isNotBlank()) {
                meanings += WordMeaning(
                    currentPos ?: "",
                    defBuf.toString().trim()
                )
                currentPos = null
                defBuf.clear()
            }
        }

        for (node: Node in span.childNodes()) {
            when {
                node is Element && node.hasClass("wordtype") -> {
                    flush()
                    currentPos = node.text()
                }

                node is Element && node.hasClass("liju") -> {}

                else -> {
                    val txt = Jsoup.parse(node.outerHtml()).text().trim()
                    if (txt.isNotEmpty()) {
                        defBuf.append(txt)
                    }
                }
            }
        }
        flush()

        return meanings
    }
}

private fun createClientWithCertificate(certInputStream: InputStream): HttpClient {
    val certFactory = CertificateFactory.getInstance("X.509")
    val caCert = certFactory.generateCertificate(certInputStream)

    val ks = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry("missingCA", caCert)
    }

    val tm = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(ks) }
        .trustManagers.filterIsInstance<X509TrustManager>().firstOrNull()

    val client = HttpClient(CIO) {
        engine {
            https {
                trustManager = tm
            }
        }
    }

    return client
}