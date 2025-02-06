package com.coda.situlearner.infra.subkit.parser

import com.coda.situlearner.core.model.infra.RawSubtitle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class AssFileParserTest {

    private val parser = AssFileParser()
    private val file = this.javaClass.classLoader?.getResource("example.ass")?.path!!

    @Test
    fun `test assFile`() = runTest {
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
                endTimeInMs = 6120L,
            ),
            RawSubtitle(
                text = "直说吧 好吗\nJust give it to me straight, okay?",
                startTimeInMs = 44200L,
                endTimeInMs = 46530L,
            ),
        ).toTypedArray()
        Assert.assertArrayEquals(expected, actual.toTypedArray())
    }
}