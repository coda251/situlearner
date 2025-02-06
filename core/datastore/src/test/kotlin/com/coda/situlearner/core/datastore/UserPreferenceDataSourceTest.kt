package com.coda.situlearner.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals

class UserPreferenceDataSourceTest : KoinTest {

    private val userPreferenceDataSource: UserPreferenceDataSource by inject()

    @Before
    fun setup() {
        startKoin {
            modules(
                // override for in memory data store
                module {
                    single<DataStore<UserPreferenceProto>> {
                        InMemoryDataStore(UserPreferenceProto.getDefaultInstance())
                    }

                    single<UserPreferenceDataSource> {
                        LocalUserPreferenceDataSource(get())
                    }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `test preferenceDataSource`() = runTest {
        assertNotNull(userPreferenceDataSource)

        var preferenceProto = userPreferenceDataSource.userPreferenceProto.first()
        assertEquals(LanguageProto.LANGUAGE_UNSPECIFIED, preferenceProto.wordFilterLanguage)
        assertEquals(WordCategoryTypeProto.WORD_CATEGORY_TYPE_UNSPECIFIED, preferenceProto.wordCategoryType)

        userPreferenceDataSource.setWordFilterLanguageProto(LanguageProto.LANGUAGE_ENGLISH)
        userPreferenceDataSource.setWordCategoryTypeProto(WordCategoryTypeProto.WORD_CATEGORY_TYPE_MEDIA)
        preferenceProto = userPreferenceDataSource.userPreferenceProto.first()
        assertEquals(LanguageProto.LANGUAGE_ENGLISH, preferenceProto.wordFilterLanguage)
        assertEquals(WordCategoryTypeProto.WORD_CATEGORY_TYPE_MEDIA, preferenceProto.wordCategoryType)
    }
}