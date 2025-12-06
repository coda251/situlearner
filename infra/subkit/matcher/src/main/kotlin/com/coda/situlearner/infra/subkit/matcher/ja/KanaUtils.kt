package com.coda.situlearner.infra.subkit.matcher.ja

internal object KanaUtils {

    private val ROW_MAPPING = mapOf(
        'あ' to 0, 'い' to 0, 'う' to 0, 'え' to 0, 'お' to 0,
        'か' to 1, 'き' to 1, 'く' to 1, 'け' to 1, 'こ' to 1,
        'さ' to 2, 'し' to 2, 'す' to 2, 'せ' to 2, 'そ' to 2,
        'た' to 3, 'ち' to 3, 'つ' to 3, 'て' to 3, 'と' to 3,
        'な' to 4, 'に' to 4, 'ぬ' to 4, 'ね' to 4, 'の' to 4,
        'は' to 5, 'ひ' to 5, 'ふ' to 5, 'へ' to 5, 'ほ' to 5,
        'ま' to 6, 'み' to 6, 'む' to 6, 'め' to 6, 'も' to 6,
        'や' to 7, 'ゆ' to 7, 'よ' to 7,
        'ら' to 8, 'り' to 8, 'る' to 8, 'れ' to 8, 'ろ' to 8,
        'わ' to 9, 'を' to 9, 'ん' to 10
    )

    private val COL_MAPPING = mapOf(
        'あ' to 0,
        'か' to 0,
        'さ' to 0,
        'た' to 0,
        'な' to 0,
        'は' to 0,
        'ま' to 0,
        'や' to 0,
        'ら' to 0,
        'わ' to 0,
        'い' to 1,
        'き' to 1,
        'し' to 1,
        'ち' to 1,
        'に' to 1,
        'ひ' to 1,
        'み' to 1,
        'り' to 1,
        'う' to 2,
        'く' to 2,
        'す' to 2,
        'つ' to 2,
        'ぬ' to 2,
        'ふ' to 2,
        'む' to 2,
        'ゆ' to 2,
        'る' to 2,
        'え' to 3,
        'け' to 3,
        'せ' to 3,
        'て' to 3,
        'ね' to 3,
        'へ' to 3,
        'め' to 3,
        'れ' to 3,
        'お' to 4,
        'こ' to 4,
        'そ' to 4,
        'と' to 4,
        'の' to 4,
        'ほ' to 4,
        'も' to 4,
        'よ' to 4,
        'ろ' to 4,
        'を' to 4,
        'ん' to 5
    )

    private val BASE_CHAR_MAPPING = mapOf(
        'ぁ' to 'あ', 'ぃ' to 'い', 'ぅ' to 'う', 'ぇ' to 'え', 'ぉ' to 'お',
        'が' to 'か', 'ぎ' to 'き', 'ぐ' to 'く', 'げ' to 'け', 'ご' to 'こ',
        'ざ' to 'さ', 'じ' to 'し', 'ず' to 'す', 'ぜ' to 'せ', 'ぞ' to 'そ',
        'だ' to 'た', 'ぢ' to 'ち', 'づ' to 'つ', 'で' to 'て', 'ど' to 'と',
        'ば' to 'は', 'び' to 'ひ', 'ぶ' to 'ふ', 'べ' to 'へ', 'ぼ' to 'ほ',
        'ぱ' to 'は', 'ぴ' to 'ひ', 'ぷ' to 'ふ', 'ぺ' to 'へ', 'ぽ' to 'ほ',
        'っ' to 'つ', 'ゃ' to 'や', 'ゅ' to 'ゆ', 'ょ' to 'よ', 'ゎ' to 'わ'
    )

    fun getBaseChar(c: Char) = BASE_CHAR_MAPPING[c] ?: c

    fun isSameRow(c1: Char, c2: Char): Boolean {
        val base1 = getBaseChar(c1)
        val base2 = getBaseChar(c2)
        val r1 = ROW_MAPPING[base1] ?: -1
        val r2 = ROW_MAPPING[base2] ?: -2
        return r1 == r2
    }

    fun isSameCol(c1: Char, c2: Char): Boolean {
        val base1 = getBaseChar(c1)
        val base2 = getBaseChar(c2)
        val col1 = COL_MAPPING[base1] ?: -1
        val col2 = COL_MAPPING[base2] ?: -2
        return col1 == col2
    }

    // we consider a subset of hiragana characters (rather than '\u3040'..'\u309F')
    fun Char.isHiragana(): Boolean = this in '\u3041'..'\u3096'

    // we consider a subset of katakana characters (rather than '\u30A0'..'\u30FF')
    fun Char.isKatakana(): Boolean = this in '\u30A1'..'\u30F6'

    // only cjk basic
    fun Char.isKanji(): Boolean = this in '\u4E00'..'\u9FFF'

    fun Char.toHiragana(): Char = when {
        this.isKatakana() -> this - 0x60
        else -> this
    }

    fun normalizeLemma(s: String): String {
        val sb = StringBuilder()
        var lastChar: Char? = null

        for (ch in s) {
            val out: Char? = when {
                ch.isHiragana() || ch.isKanji() -> ch
                ch.isKatakana() -> ch.toHiragana()
                ch == '々' -> lastChar
                ch == 'ー' -> ch
                // do not consider：
                // 1. 'ゝ', 'ヽ', 'ゞ', 'ヾ'
                // 2. half width katakana
                else -> null
            }

            if (out != null) {
                sb.append(out)
                lastChar = out
            }
        }

        return sb.toString()
    }

    fun normalizePronunciation(s: String): String = buildString {
        for (ch in s) {
            when {
                ch.isHiragana() -> append(ch)
                ch.isKatakana() -> append(ch.toHiragana())
                ch == 'ー' -> append(ch)
            }
        }
    }
}