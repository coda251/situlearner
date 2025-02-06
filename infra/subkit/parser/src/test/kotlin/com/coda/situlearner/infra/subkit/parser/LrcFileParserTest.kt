package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class LrcFileParserTest {

    private val parser = LrcFileParser()
    private val file = this.javaClass.classLoader?.getResource("example.lrc")?.path!!

    @Test
    fun `test lrcFile`() = runTest {
        val actual = parser.parse(file)
        val expected = listOf(
            RawSubtitle(
                text = "What a good day! Wanna go out?",
                startTimeInMs = 2120L,
                endTimeInMs = 4820L,
            ),
            RawSubtitle(
                text = "No, I'm busy preparing my math test recently. I'm afraid I can't pass it.",
                startTimeInMs = 4820L,
                endTimeInMs = Long.MAX_VALUE,
            ),
        ).toTypedArray()
        Assert.assertArrayEquals(expected, actual.toTypedArray())
    }
}