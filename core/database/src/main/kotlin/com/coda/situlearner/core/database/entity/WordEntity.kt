package com.coda.situlearner.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coda.situlearner.core.database.model.Language
import com.coda.situlearner.core.database.model.Meanings
import com.coda.situlearner.core.database.model.WordProficiency
import kotlinx.datetime.Instant

@Entity
data class WordEntity(
    @PrimaryKey val id: String,
    val word: String,
    val language: Language,
    val dictionaryName: String?,
    val pronunciation: String?,
    val meanings: Meanings?,
    val lastViewedDate: Instant?,
    val createdDate: Instant,
    val meaningProficiency: WordProficiency,
    val translationProficiency: WordProficiency?,
)