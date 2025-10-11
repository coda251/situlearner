package com.coda.situlearner.core.model.data.mapper

import com.coda.situlearner.core.model.data.Word
import com.coda.situlearner.core.model.data.WordProficiency
import com.coda.situlearner.core.model.data.WordProficiencyType
import com.coda.situlearner.core.model.data.WordWithContexts
import com.coda.situlearner.core.model.infra.WordInfo

fun Word.asWordInfo() = WordInfo.fromDb(
    word = word,
    dictionaryName = dictionaryName,
    pronunciation = pronunciation,
    meanings = meanings
)

val List<WordWithContexts>.proficiencyType: WordProficiencyType
    get() {
        val hasWords = isNotEmpty()
        val allMeaningProficient =
            hasWords && all { it.word.meaningProficiency == WordProficiency.Proficient }
        return if (allMeaningProficient) WordProficiencyType.Translation else WordProficiencyType.Meaning
    }

val Word.proficiencyType: WordProficiencyType
    get() = if (meaningProficiency == WordProficiency.Proficient) WordProficiencyType.Translation
    else WordProficiencyType.Meaning