package com.coda.situlearner.core.database

import com.coda.situlearner.core.database.dao.MediaLibraryDao
import com.coda.situlearner.core.database.dao.WordBankDao
import com.coda.situlearner.core.database.entity.MediaCollectionWithFilesEntity
import com.coda.situlearner.core.database.entity.WordWithContextsEntity
import com.coda.situlearner.core.database.model.Unknown_Language
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class WordBankDaoTest {

    private val mediaLibraryDao: MediaLibraryDao by inject(MediaLibraryDao::class.java)
    private val wordBankDao: WordBankDao by inject(WordBankDao::class.java)

    @Before
    fun `prepare media entity`() = runTest {
        val mediaCollectionWithFilesEntity = MediaCollectionWithFilesEntity(
            mediaCollections[0],
            mediaFiles
        )
        mediaLibraryDao.insertMediaCollectionWithFilesEntity(mediaCollectionWithFilesEntity)
    }

    @Test
    fun `test getWordWithContextsEntities`() = runTest {

        val wordEntity = words[0]
        val wordContextEntity = wordContexts[0]

        wordBankDao.insertWordEntityAndWordContextEntity(wordEntity, wordContextEntity)
        val result: List<WordWithContextsEntity> =
            wordBankDao.getWordWithContextsEntities(language = Unknown_Language).first()
        assertEquals(1, result.size)
        assertEquals(1, result[0].contexts.size)
        assertEquals(
            mapOf(
                "noun" to "this is a noun",
                "v." to "this is a verb"
            ), result[0].word.meanings
        )
        assertEquals("file_url_0", result[0].contexts[0].mediaFile?.url)
    }
}