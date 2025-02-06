package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SrtFileParserTest {

    private val parser = SrtFileParser()
    private val file = this.javaClass.classLoader?.getResource("example.srt")?.path!!

    @Test
    fun `test srtFile`() = runTest {
        val actual = parser.parse(file)
        val expected = listOf(
            RawSubtitle(
                text = "What a good day! Wanna go out?",
                startTimeInMs = 2122L,
                endTimeInMs = 4818L,
            ),
            RawSubtitle(
                text = "No, I'm busy preparing my math test recently.\n" +
                        "I'm afraid I can't pass it.",
                startTimeInMs = 4818L,
                endTimeInMs = 6122L,
            ),
        ).toTypedArray()
        Assert.assertArrayEquals(expected, actual.toTypedArray())
    }
}